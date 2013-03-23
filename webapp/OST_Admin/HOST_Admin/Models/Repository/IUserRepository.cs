using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace HOST_Admin.Models.Repository
{
    public interface IUserRepository
    {
        int authenticateUser(String user_name, String password);
        Role getRoleByUserId(int user_id);
        int getLoggedInUserId();
        String getLoggedInRole();
        void setLoggedInUserId(int user_id);
        void setLoggedInRole(String role);
        void logOutUser();
        IQueryable<User> getAllUsers();
        User getUserById(int user_id);
        IQueryable<Role> getAllRoles();
        Role getRoleById(int role_id);
        void addUser(User user, int role_id);
        void updateUser(User user, int role_id);
        void deleteUser(User user);
    }
}