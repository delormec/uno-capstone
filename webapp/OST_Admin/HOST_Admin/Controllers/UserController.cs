using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using HOST_Admin.Models;
using HOST_Admin.Models.Repository;

namespace HOST_Admin.Controllers
{
    [Authorize]
    public class UserController : Controller
    {

        private HOSTDataContext db = new HOSTDataContext();
        private readonly IUserRepository _userRepository;

        public UserController(IUserRepository userRepository)
        {
            _userRepository = userRepository;
        }

        //
        // GET: /User/

        public ActionResult Index()
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");
            return View(_userRepository.getAllUsers());
        }

        public ActionResult Edit(int id)
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");
            //set up dropdown
            SelectList sl = new SelectList(_userRepository.getAllRoles(), "RoleId", "Name", _userRepository.getRoleByUserId(id).RoleId);
            //need all roles so we can change a user's role --if needed
            //ViewBag.Roles = _userRepository.getAllRoles();
            ViewBag.RoleSelect = sl;

            return View(_userRepository.getUserById(id));
        }

        [HttpPost]
        public ActionResult Edit(User user, int role_id)
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");

            _userRepository.updateUser(user, role_id);
            return RedirectToAction("Index");
        }

        public ActionResult Add()
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");

            SelectList sl = new SelectList(_userRepository.getAllRoles(), "RoleId", "Name");
            ViewBag.RoleSelect = sl;

            return View();
        }

        [HttpPost]
        public ActionResult Add(User user, int role_id)
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");

            _userRepository.addUser(user, role_id);

            return RedirectToAction("Index");
        }

        public ActionResult Delete(int id)
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");

            User user;
            user = _userRepository.getUserById(id);
            _userRepository.deleteUser(user);

            return RedirectToAction("Index");
        }
    }
}
