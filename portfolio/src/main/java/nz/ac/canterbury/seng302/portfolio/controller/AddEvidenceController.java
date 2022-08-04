package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.WebLink;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * The controller for handling backend of the add evidence page
 */
@Controller
public class AddEvidenceController {

    private static final String ADD_EVIDENCE = "addEvidence";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserAccountClientService userService;

    @Autowired
    private PortfolioUserService portfolioUserService;

    @Autowired
    private EvidenceService evidenceService;

    private static final String TIMEFORMAT = "yyyy-MM-dd";

    /**
     * Display the add evidence page.
     * @param principal Authentication state of client
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The add evidence page.
     */
    @GetMapping("/addEvidence")
    public String addEvidence(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);

        int userId = userService.getUserId(principal);
        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();
        Project project = projectService.getProjectById(projectId);

        Evidence evidence;

        Date evidenceDate;
        Date currentDate = new Date();

        if(currentDate.after(project.getStartDate()) && currentDate.before(project.getEndDate())) {
            evidenceDate = currentDate;
        } else {
            evidenceDate = project.getStartDate();
        }

        evidence = new Evidence(userId, projectId, "", "", evidenceDate);

        model.addAttribute("evidenceTitle", evidence.getTitle());
        model.addAttribute("evidenceDescription", evidence.getDescription());
        model.addAttribute("evidenceDate", Project.dateToString(evidence.getDate(), TIMEFORMAT));
        model.addAttribute("minEvidenceDate", Project.dateToString(project.getStartDate(), TIMEFORMAT));
        model.addAttribute("maxEvidenceDate", Project.dateToString(project.getEndDate(), TIMEFORMAT));
        return ADD_EVIDENCE;
    }

    /**
     * Save a piece of evidence. It will be rejected silently if the data given is invalid, otherwise it will be saved.
     * If saved, the user will be taken to their portfolio page.
     * @param principal Authentication state of client
     * @param title The title of the piece of evidence
     * @param description The description of the piece of evidence
     * @param dateString The date the evidence occurred, in yyyy-MM-dd string format
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return A redirect to the portfolio page, or staying on the add evidence page
     */
    @PostMapping("/addEvidence")
    public String saveEvidence(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="evidenceTitle") String title,
            @RequestParam(name="evidenceDescription") String description,
            @RequestParam(name="evidenceDate") String dateString,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        int projectId = portfolioUserService.getUserById(user.getId()).getCurrentProject();
        Project project = projectService.getProjectById(projectId);

        model.addAttribute("user", user);
        model.addAttribute("minEvidenceDate", Project.dateToString(project.getStartDate(), TIMEFORMAT));
        model.addAttribute("maxEvidenceDate", Project.dateToString(project.getEndDate(), TIMEFORMAT));

        Date date;
        try {
            date = new SimpleDateFormat(TIMEFORMAT).parse(dateString);
        } catch (ParseException exception) {

            return ADD_EVIDENCE; // Fail silently as client has responsibility for error checking
        }
        int userId = userService.getUserId(principal);
        Evidence evidence = new Evidence(userId, projectId, title, description, date);
        try {
            evidenceService.saveEvidence(evidence);
        } catch (IllegalArgumentException exception) {
            if (Objects.equals(exception.getMessage(), "Title not valid")) {
                model.addAttribute("titleError", "Title cannot be all special characters");
            } else if (Objects.equals(exception.getMessage(), "Description not valid")) {
                model.addAttribute("descriptionError", "Description cannot be all special characters");
            } else if (Objects.equals(exception.getMessage(), "Date not valid")) {
                model.addAttribute("dateError", "Date must be within the project dates");
            } else {
                model.addAttribute("generalError", exception.getMessage());
            }
            model.addAttribute("evidenceTitle", evidence.getTitle());
            model.addAttribute("evidenceDescription", evidence.getDescription());
            model.addAttribute("evidenceDate", Project.dateToString(evidence.getDate(), TIMEFORMAT));
            return ADD_EVIDENCE; // Fail silently as client has responsibility for error checking
        }
        return "redirect:/portfolio";
    }

    /**
     * Save one web link. Redirects to portfolio page with no message if evidence does not exist.
     * If correctly saved, redirects to projects page.
     * @param principal Authentication state of client
     * @param evidenceId Id of the evidence to add the link to
     * @param webLink The link string to be added to evidence of id=evidenceId
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Redirect to portfolio page.
     */
    @PostMapping("/addWebLink")
    public String addWebLink(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="projectId") String evidenceId,
            @RequestParam(name="webLink") String webLink,
            @RequestParam(name="webLinkName") String webLinkName,
            Model model
    ) {
        int id = Integer.parseInt(evidenceId);
        try {
            WebLink webLink1;
            if (webLinkName.isEmpty()) {
                webLink1 = new WebLink(webLink);
            } else {
                webLink1 = new WebLink(webLink, webLinkName);
            }
            evidenceService.saveWebLink(id, webLink1);
        } catch (NoSuchElementException e) {
            return "redirect:/portfolio";
        }
        return "redirect:/portfolio";
    }

}

