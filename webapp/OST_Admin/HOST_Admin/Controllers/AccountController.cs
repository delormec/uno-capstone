using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using HOST_Admin.Models;
using HOST_Admin.Models.Repository;
using System.Web.Security;
using System.Security.Principal;

namespace HOST_Admin.Controllers
{
    /// <summary>
    /// Handles Login/Logout functionality, sets cookies ect. The only controller that does not require use of [Authorize].
    /// </summary>
    public class AccountController : Controller
    {
        /// <summary>
        /// Reference to user repository.
        /// </summary>
        private readonly IUserRepository _userRepository;

        /// <summary>
        /// Base constructor, MVC calls this constructor behind the scenes.
        /// </summary>
        /// <param name="userRepository">It is possible to pass something that implements this interface for testing purposes.</param>
        public AccountController(IUserRepository userRepository)
        {
            _userRepository = userRepository;
        }

        /// <summary>
        /// GET: Launch pad of the admin tool.
        /// </summary>
        /// <param name="user_name">Passed back from LogIn:POST if login failed.</param>
        /// <param name="failed">Set to true if login failed, allows view to display error message.</param>
        /// <returns></returns>
        public ActionResult LogIn(String user_name, bool? failed)
        {
            if (failed == true)
                ViewBag.failed = failed;

            if (user_name != "")
                ViewBag.user_name = user_name;

            return View();
        }

        /// <summary>
        /// POST: User submits username/password. Checks if user is authenticated and sets session variables related to user.
        /// </summary>
        /// <param name="user_name">Plain text username from LogIn</param>
        /// <param name="password">Plain text password from LogIn</param>
        /// <returns>Goes to Form/Index if successful, back to Account/LogIn if failed.</returns>
        [HttpPost]
        public ActionResult LogIn(String user_name, String password)
        {
            //if there are no users, then we want to get to the user page so we can add more, not sure this is a good idea
            if (_userRepository.getAllUsers().Count() == 0)
            {
                FormsAuthentication.SetAuthCookie("admin", false);
                _userRepository.setLoggedInUserId(0);
                _userRepository.setLoggedInRole("Administrator");
                return RedirectToAction("Index", "Form");
            }

            //Authenticate user
            int user_id = _userRepository.authenticateUser(user_name, password);

            //If login failed send back to main page with error
            if (user_id == -1)
                return RedirectToAction("LogIn", new {user_name = user_name, failed = true });

            //set session variables
            _userRepository.setLoggedInUserId(user_id);
            _userRepository.setLoggedInRole(_userRepository.getRoleByUserId(user_id).Name);

            //set cookie for authorization
            FormsAuthentication.SetAuthCookie(user_name, false);

            return RedirectToAction("Index", "Form");
        }

        /// <summary>
        /// Logs out of the system, removes session variables ect.
        /// </summary>
        /// <returns>Goes to Account/Login</returns>
        public ActionResult LogOut()
        {
            FormsAuthentication.SignOut();
            _userRepository.logOutUser();

            return RedirectToAction("LogIn");
        }

    }
}
