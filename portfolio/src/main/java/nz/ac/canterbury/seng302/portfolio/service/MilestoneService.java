package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MilestoneService {
    @Autowired
    private MilestoneRepository milestoneRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectEditsService projectEditsService;

    /**
     * Get a list of all milestones
     * @return the list of all existing milestones
     */
    public List<Milestone> getAllMilestones() {
        return (List<Milestone>) milestoneRepository.findAll();
    }

    /**
     * Get the milestone by id
     * @param milestoneId the id of the milestone
     * @return the milestone which has the required id
     * @throws IllegalArgumentException when milestone is not found
     */
    public Milestone getMilestoneById(Integer milestoneId) throws IllegalArgumentException {
        Optional<Milestone> milestone = milestoneRepository.findById(milestoneId);
        if (milestone.isPresent()) {
            return milestone.get();
        } else {
            throw new IllegalArgumentException("Milestone not found");
        }
    }

    /**
     * Get milestone by parent project id
     * @param projectId the id of the project
     * @return the list of milestones by the project id
     */
    public List<Milestone> getByMilestoneParentProjectId(int projectId) {
        return milestoneRepository.findByMilestoneParentProjectIdOrderByMilestoneDate(projectId);
    }

    /**
     * Save the milestone to the repository
     */
    public void saveMilestone(Milestone milestone) {
        projectEditsService.refreshProject(milestone.getMilestoneParentProjectId());
        milestoneRepository.save(milestone);
    }

    /**
     * Deletes the milestone from the repository using the provided id
     *
     * @param milestoneId the id of the deadline to be deleted
     */
    public void deleteMilestoneById(int milestoneId) {
        if (milestoneRepository.findById(milestoneId) == null) {
            throw new UnsupportedOperationException("Milestone does not exist");
        }
        milestoneRepository.deleteById(milestoneId);
    }

    /**
     * Updates the milestone's date and name attributes
     * @param parentProjectId The parent project of the milestone
     * @param milestoneId The milestone ID
     * @param milestoneName The new deadline name
     * @param milestoneDate The new deadline date
     * @throws Exception Throws UnsupportedOperationException is the new date doesn't fall within the parent project dates
     */
    public void updateMilestone(int parentProjectId, int milestoneId, String milestoneName, Date milestoneDate) throws Exception {
        Milestone milestone = getMilestoneById(milestoneId);
        Project parentProject = projectService.getProjectById(parentProjectId);
        Date projectStartDate = parentProject.getStartDate();
        Date projectEndDate = parentProject.getEndDate();
        if (milestoneDate.compareTo(projectEndDate) > 0 || milestoneDate.compareTo(projectStartDate) < 0) {
            throw new UnsupportedOperationException("Milestone date must be within the project dates");
        }
        milestone.setMilestoneDate(milestoneDate);
        milestone.setMilestoneName(milestoneName);
        saveMilestone(milestone);
    }

    /**
     * Creates a new milestone with the given parameters
     * @param parentProjectId The parent project of the milestone
     * @param milestoneName The new milestone name
     * @param milestoneDate The new milestone date
     * @throws Exception Throws UnsupportedOperationException is the new date doesn't fall within the parent project dates
     */
    public void createNewMilestone(int parentProjectId, String milestoneName, Date milestoneDate) throws Exception {
        Project parentProject = projectService.getProjectById(parentProjectId);
        Date projectStartDate = parentProject.getStartDate();
        Date projectEndDate = parentProject.getEndDate();
        if (milestoneDate.compareTo(projectEndDate) > 0 || milestoneDate.compareTo(projectStartDate) < 0) {
            throw new UnsupportedOperationException("Milestone date must be within the project dates");
        } else {
            saveMilestone(new Milestone(parentProjectId, milestoneName, milestoneDate));
        }
    }
}
