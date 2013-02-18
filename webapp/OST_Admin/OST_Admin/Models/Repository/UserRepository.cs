using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace OST_Admin.Models.Repository
{
    public class UserRepository : IUserRepository
    {
        OSTDataContext _databaseContext;

        public UserRepository(IOSTDataContext databaseContext)
        {
            _databaseContext = new OSTDataContext();
        }

        //returns -1 if no user found, otherwise returns their user_id
        public int authenticateUser(string user_name, string password)
        {
            //if no match, return -1 (failed)
            if (_databaseContext.Users.Where(u => u.UserName == user_name && u.Password == password).Count() == 0)
                return -1;
            else
                return _databaseContext.Users.Where(u => u.UserName == user_name && u.Password == password).Single().UserId;
        }

        //return their role by user id
        public String getRoleByUserId(int user_id)
        {
            return _databaseContext.Users.Where(u => u.UserId == user_id).Single().Role.Name;
        }

        public int getLoggedInUserId()
        {
            return (int)HttpContext.Current.Session["__UserId"];
        }

        public void setLoggedInUserId(int user_id)
        {
            HttpContext.Current.Session.Add("__UserId", user_id);
        }

        public void logOutUser()
        {
            HttpContext.Current.Session.Remove("__UserId");
        }

        public IQueryable<User> getAllUsers()
        {
            return _databaseContext.Users;
        }

        public User getUserById(int user_id)
        {
            return _databaseContext.Users.Where(u => u.UserId == user_id).Single();
        }

        public IQueryable<Role> getAllRoles()
        {
            return _databaseContext.Roles;
        }

        Role IUserRepository.getRoleById(int role_id)
        {
            return _databaseContext.Roles.Where(r => r.RoleId == role_id).Single();
        }

        public void deleteUser(int user_id, int logged_in_user_id)
        {
            //TODO
            throw new NotImplementedException();
        }

        public void addUser(User user)
        {
            //TODO
            throw new NotImplementedException();
        }
    }
}