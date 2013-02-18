using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace OST_Admin.Models.Repository
{
    public interface IUserRepository
    {
        int authenticateUser(String user_name, String password);
        String getRoleByUserId(int user_id);
        int getLoggedInUserId();
        void setLoggedInUserId(int user_id);
        void logOutUser();
        IQueryable<User> getAllUsers();
        User getUserById(int user_id);
        IQueryable<Role> getAllRoles();
        Role getRoleById(int role_id);
        void deleteUser(int user_id, int logged_in_user_id);
        void addUser(User user);
    }
}