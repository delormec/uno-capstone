using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace HOST_Admin.Models.Repository
{
    /// <summary>
    /// Model layer repository that handles all interactions with User objects
    /// </summary>
    public class UserRepository : IUserRepository
    {
        /// <summary>
        /// Interface with EntityFramework (database)
        /// </summary>
        HOSTDataContext _databaseContext;

        /// <summary>
        /// Base constructor, MVC calls this constructor behind the scenes.
        /// </summary>
        /// <param name="databaseContext">It is possible to pass something that implements this interface for testing purposes.</param>
        public UserRepository(IHOSTDataContext databaseContext)
        {
            _databaseContext = new HOSTDataContext();
        }

        /// <summary>
        /// Authenticate a user trying to access the system.
        /// </summary>
        /// <param name="user_name"></param>
        /// <param name="password">Plain text password.</param>
        /// <returns>If user is authetnicated then return the user_id, otherwise return -1 for failed login.</returns>
        public int authenticateUser(string user_name, string password)
        {
            User user;
            
            //find an active user that has the username were authenticating
            //TODO - enforce unique usernames?
            if (_databaseContext.Users.Where(u => u.UserName == user_name && u.Active == true).Count() != 0)
            {
                user = _databaseContext.Users.Where(u => u.UserName == user_name && u.Active == true).Single();
            }
            else
                return -1;

            //run the plain text password against the hashed password
            if (HOSTEncrpyption.VerifyHash(password, user.Password))
            {
                return user.UserId;
            }
            else 
                return -1;
        }

        /// <summary>
        /// Return role by user_id.
        /// </summary>
        /// <param name="user_id"></param>
        /// <returns></returns>
        public Role getRoleByUserId(int user_id)
        {
            return _databaseContext.Users.Where(u => u.UserId == user_id).Single().Role;
        }

        /// <summary>
        /// Get __UserId session variable.
        /// </summary>
        /// <returns></returns>
        public int getLoggedInUserId()
        {
            return (int)HttpContext.Current.Session["__UserId"];
        }

        /// <summary>
        /// Set __UserId session variable.
        /// </summary>
        /// <param name="user_id"></param>
        public void setLoggedInUserId(int user_id)
        {
            HttpContext.Current.Session.Add("__UserId", user_id);
        }

        /// <summary>
        /// Remove all session variables.
        /// </summary>
        public void logOutUser()
        {   
            HttpContext.Current.Session.RemoveAll();
        }


        /// <summary>
        /// Get all users.
        /// </summary>
        /// <returns></returns>
        public IQueryable<User> getAllUsers()
        {
            return _databaseContext.Users;
        }

        /// <summary>
        /// Get User by user_id.
        /// </summary>
        /// <param name="user_id"></param>
        /// <returns></returns>
        public User getUserById(int user_id)
        {
            return _databaseContext.Users.Where(u => u.UserId == user_id).Single();
        }

        /// <summary>
        /// Get all roles.
        /// </summary>
        /// <returns></returns>
        public IQueryable<Role> getAllRoles()
        {
            return _databaseContext.Roles;
        }

        /// <summary>
        /// Get role by role_id.
        /// </summary>
        /// <param name="role_id"></param>
        /// <returns></returns>
        Role IUserRepository.getRoleById(int role_id)
        {
            return _databaseContext.Roles.Where(r => r.RoleId == role_id).Single();
        }

        /// <summary>
        /// Add user to database, and it's respective role.
        /// Also hashes the password with a random salt.
        /// </summary>
        /// <param name="user"></param>
        /// <param name="role_id"></param>
        public void addUser(User user, int role_id)
        {
            //get a role, then add user to it
            Role existing_role = _databaseContext.Roles.Where(r => r.RoleId == role_id).Single();
            byte[] salt = HOSTEncrpyption.getRandomSaltBytes();

            user.Role = existing_role;

            if (user.UserName == null)
                user.UserName = "";

            user.Password = HOSTEncrpyption.ComputeHash(user.Password, salt);// +":" + Convert.ToBase64String(salt);

            _databaseContext.SaveChanges();
        }

        /// <summary>
        /// Get __UserRole session variable.
        /// </summary>
        /// <returns></returns>
        public String getLoggedInRole()
        {
            return (String)HttpContext.Current.Session["__UserRole"];
        }

        /// <summary>
        /// Set __UserRole session variable.
        /// </summary>
        /// <param name="role"></param>
        public void setLoggedInRole(string role)
        {
            HttpContext.Current.Session.Add("__UserRole", role);
        }

        /// <summary>
        /// Update a user and it's respective role.
        /// </summary>
        /// <param name="user"></param>
        /// <param name="role_id"></param>
        public void updateUser(User user, int role_id)
        {
            //get the user in database, then set all its fields to new values
            User old_user;
            old_user = getUserById(user.UserId);


            //if password is blank do nothing, if its get a random salt, and hash the plain text
            if (user.Password == null)
            {
                //do nothing, they didn't want to change the password
            }
            else
            {
                byte[] salt = HOSTEncrpyption.getRandomSaltBytes();
                old_user.Password = HOSTEncrpyption.ComputeHash(user.Password, salt);// +":" + Convert.ToBase64String(salt);
            }

            //old_user.Password = user.Password;
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

        /// <summary>
        /// Delete a user, removes it from database.
        /// </summary>
        /// <param name="user"></param>
        public void deleteUser(User user)
        {
            _databaseContext.DeleteObject(user);
            _databaseContext.SaveChanges();
        }
    }
}