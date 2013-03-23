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
    public class AccountController : Controller
    {
        private HOSTDataContext db = new HOSTDataContext();
        private readonly IUserRepository _userRepository;

        public AccountController(IUserRepository userRepository)
        {
            _userRepository = userRepository;
        }
        //
        // GET: /Account/

        public ActionResult LogIn(String user_name, bool? failed)
        {
            if (failed == true)
                ViewBag.failed = failed;

            if (user_name != "")
                ViewBag.user_name = user_name;

            return View();
        }

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

            int user_id = _userRepository.authenticateUser(user_name, password);
            if (user_id == -1)
                return RedirectToAction("LogIn", new {user_name = user_name, failed = true });

            //set session variables
            _userRepository.setLoggedInUserId(user_id);
            _userRepository.setLoggedInRole(_userRepository.getRoleByUserId(user_id).Name);

            //set cookie for authorization
            FormsAuthentication.SetAuthCookie(user_name, false);

            //does not seem like good idea
            //Roles.AddUserToRole(user_name, _userRepository.getRoleByUserId(user_id).Name);
            //Roles.AddUserToRole("admin", "Administrator");
            //System.Web.HttpContext.Current.rol


            // Get Forms Identity From Current User
            //IIdentity id = (IIdentity)System.Web.HttpContext.Current.User.Identity;
            // Get Forms Ticket From Identity object
            //FormsAuthenticationTicket ticket = id.Ticket;
            // Retrieve stored user-data (our roles from db)
            //string userData = ticket.UserData;
            //string[] roles = userData.Split(',');
            // Create a new Generic Principal Instance and assign to Current User
            //System.Web.HttpContext.Current.User = new GenericPrincipal(id, new String[] { "Administrator" });


            return RedirectToAction("Index", "Form");
        }

        public ActionResult LogOut()
        {
            FormsAuthentication.SignOut();
            _userRepository.logOutUser();

            return RedirectToAction("LogIn");
        }

    }
}
