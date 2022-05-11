package nz.ac.canterbury.seng302.portfolio.model;

import io.cucumber.java.an.Y;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserListResponseTests {

    // Tests that creating a UserListResponse from a PaginatedUserResponse carries all the information over properly.
    @Test
    void testCreateUserListResponse() {
        UserResponse userResponse = UserResponse.newBuilder().setFirstName("test").build();
        PaginatedUsersResponse source = PaginatedUsersResponse.newBuilder().addUsers(userResponse).build();
        UserListResponse response = new UserListResponse(source);
        assertEquals(source.getResultSetSize(), response.getResultSetSize());
        assertEquals(userResponse.getFirstName(), response.getUsers().get(0).getFirstName());
    }

}