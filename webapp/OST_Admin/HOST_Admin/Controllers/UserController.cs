using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using HOST_Admin.Models;
using HOST_Admin.Models.Repository;

namespace HOST_Admin.Controllers
{
    /// <summary>
    /// Handles the management of users. [Authorize] tag requires that only authorized users are allowed.
    /// Furthermore only Adminstrator user's are allowed to access this Controller.
    /// </summary>
    [Authorize]
    public class UserController : Controller
    {
        /// <summary>
        /// Reference to user repository.
        /// </summary>
        private readonly IUserRepository _userRepository;

        /// <summary>
        /// Base constructor, MVC calls this constructor behind the scenes.
        /// </summary>
        /// <param name="userRepository">It is possible to pass something that implements this interface for testing purposes.</param>
        public UserController(IUserRepository userRepository)
        {
            _userRepository = userRepository;
        }

        /// <summary>
        /// GET: User/Index: Displays a list of current users in the system. Provides access to various user management tools.
        /// </summary>
        /// <returns>If not an Administrator log out of the system, otherwise display index.</returns>
        public ActionResult Index()
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");
            return View(_userRepository.getAllUsers());
        }

        /// <summary>
        /// GET: User/Edit: Provides interface to edit a user.
        /// </summary>
        /// <param name="id">User id to be edited.</param>
        /// <returns>If not an Administrator log out of the system, otherwise display edit page.</returns>
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

        /// <summary>
        /// POST: User/Edit: Updates user, maybe also update their role.
        /// </summary>
        /// <param name="user">User to be updated.</param>
        /// <param name="role_id">New role of updated user.</param>
        /// <returns>If not an Administrator log out of the system, otherwise save and redirect to index.</returns>
        [HttpPost]
        public ActionResult Edit(User user, int role_id)
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");

            _userRepository.updateUser(user, role_id);
            return RedirectToAction("Index");
        }

        /// <summary>
        /// GET: User/Add: Interface to add a new user.
        /// </summary>
        /// <returns>If not an Administrator log out of the system, otherwise provides interface for adding a new user.</returns>
        public ActionResult Add()
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");

            //setup dropdown
            SelectList sl = new SelectList(_userRepository.getAllRoles(), "RoleId", "Name");
            ViewBag.RoleSelect = sl;

            return View();
        }

        /// <summary>
        /// POST: User/Add: Save new user.
        /// </summary>
        /// <param name="user">New user.</param>
        /// <param name="role_id">New user's role.</param>
        /// <returns>If not an Administrator log out of the system, otherwise add user and redirect to index.</returns>
        [HttpPost]
        public ActionResult Add(User user, int role_id)
        {
            if (_userRepository.getLoggedInRole() != "Administrator")
                return RedirectToAction("LogOut", "Account");

            _userRepository.addUser(user, role_id);

            return RedirectToAction("Index");
        }

        /// <summary>
        /// GET: User/Delete: Delete a user.
        /// </summary>
        /// <param name="id">User id to be deleted.</param>
        /// <returns>If not an Administrator log out of the system, otherwise delete the user and return to the index.</returns>
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
