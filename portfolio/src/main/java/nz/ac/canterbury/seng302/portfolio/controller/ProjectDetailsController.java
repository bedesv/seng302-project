package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProjectDetailsController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private UserAccountClientService userAccountClientService;


    @GetMapping("/projects/{id}")
    public String projectDetails(@AuthenticationPrincipal AuthState principal, Model model, @PathVariable("id") String id) throws Exception {
        // Add user details to model
        Integer userId = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        /* Add project details to the model */
        int projectId = Integer.parseInt(id);
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        List<Sprint> sprintList = sprintService.getByParentProjectId(projectId);
        model.addAttribute("sprints", sprintList);


        /* Return the name of the Thymeleaf template
        detects the role of the current user and returns appropriate page
        System.out.println(role);*/
        String role = userAccountClientService.getRole(principal);
        if (role.contains("teacher")) {
            return "teacherProjectDetails";

        } else {
            return "userProjectDetails";
        }
    }

}
