package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.GroupListResponse;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AddGroupMembersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class GroupsController {
    @Autowired
    private UserAccountClientService userAccountClientService;
    @Autowired
    private GroupsClientService groupsClientService;

    private static final String GROUPS_PAGE = "groups";

    /**
     * Get mapping to fetch groups page
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The user or teacher groups html page depending on the logged in user
     */
    @GetMapping("/groups")
    public String groups(@AuthenticationPrincipal AuthState principal, Model model){
        int id = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        model.addAttribute("userIsTeacher", userAccountClientService.isTeacher(principal));
        GroupListResponse groups = groupsClientService.getAllGroups();
        model.addAttribute("groups", groups.getGroups());
        return GROUPS_PAGE;
    }

    @RequestMapping(value = "/groups/addMembers", method=RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String saveGroupEdits(@AuthenticationPrincipal AuthState principal,
                                 @RequestParam("groupId") Integer groupId,
                                 @RequestParam(value="members") List<Integer> members,
                                 Model model) {

        boolean userIsTeacher = userAccountClientService.isTeacher(principal);
        //Check if it is a teacher making the request
        if (!userIsTeacher) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to make these changes\n");
        } else {
            groupsClientService.addGroupMembers(groupId, members);
            Group group = new Group(groupsClientService.getGroupDetailsById(groupId));
            model.addAttribute("group", group);
            model.addAttribute("userIsTeacher", userIsTeacher);

        }
        return "groupTable";
    }

}
