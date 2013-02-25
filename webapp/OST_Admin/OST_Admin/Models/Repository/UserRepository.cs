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
            if (_databaseContext.Users.Where(u => u.UserName == user_name && u.Password == password && u.Active == true).Count() == 0)
                return -1;
            else
                return _databaseContext.Users.Where(u => u.UserName == user_name && u.Password == password && u.Active == true).Single().UserId;
        }

        //return their role by user id
        public Role getRoleByUserId(int user_id)
        {
            return _databaseContext.Users.Where(u => u.UserId == user_id).Single().Role;
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
            HttpContext.Current.Session.RemoveAll();
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

        public void addUser(User user, int role_id)
        {
            //get a role, then add user to it
            Role existing_role = _databaseContext.Roles.Where(r => r.RoleId == role_id).Single();

            user.Role = existing_role;

            if (user.UserName == null)
                user.UserName = "";

            if (user.Password == null)
                user.Password = "";

            _databaseContext.SaveChanges();
        }

        public String getLoggedInRole()
        {
            return (String)HttpContext.Current.Session["__UserRole"];
        }

        public void setLoggedInRole(string role)
        {
            HttpContext.Current.Session.Add("__UserRole", role);
        }

        public void updateUser(User user, int role_id)
        {
            //get the user in database, then set all its fields to new values
            User old_user;
            old_user = getUserById(user.UserId);

            if (user.Password == null)
                user.Password = "";

            old_user.Password = user.Password;
            old_user.UserName = user.UserName;
            old_user.Active = user.Active;


            //temp remove the user from the database so it can be assigned to the correct role
            //turns out we don't need to do this
            //_databaseContext.Detach(old_user);

            //get a role, then add user to it
            Role existing_role = _databaseContext.Roles.Where(r => r.RoleId == role_id).Single();

            //attach the user to it's new role (may have not changed)
            old_user.Role = existing_role;

            //if were deleting a user, we need to make sure their forms are not orphened
            //well maybe it doesn't matter, the admin will always be able to see it
            //if (old_user.Deleted == false && user.Deleted == true)
            //{
            //   //do something    
            //}

            _databaseContext.SaveChanges();
        }


        public void deleteUser(User user)
        {
            _databaseContext.DeleteObject(user);
            _databaseContext.SaveChanges();
        }
    }
}