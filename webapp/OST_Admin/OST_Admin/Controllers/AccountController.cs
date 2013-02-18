using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using OST_Admin.Models;
using OST_Admin.Models.Repository;
using System.Web.Security;

namespace OST_Admin.Controllers
{
    public class AccountController : Controller
    {
        private OSTDataContext db = new OSTDataContext();
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
            int user_id = _userRepository.authenticateUser(user_name, password);
            if (user_id == -1)
                return RedirectToAction("LogIn", new {user_name = user_name, failed = true });

            _userRepository.setLoggedInUserId(user_id);

            //set cookie for authorization
            FormsAuthentication.SetAuthCookie(user_name, false);

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
