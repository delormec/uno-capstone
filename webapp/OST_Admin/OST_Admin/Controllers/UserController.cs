using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using OST_Admin.Models;
using OST_Admin.Models.Repository;

namespace OST_Admin.Controllers
{
    [Authorize]
    public class UserController : Controller
    {

        private OSTDataContext db = new OSTDataContext();
        private readonly IUserRepository _userRepository;

        public UserController(IUserRepository userRepository)
        {
            _userRepository = userRepository;
        }

        //
        // GET: /User/

        public ActionResult Index()
        {
            return View(_userRepository.getAllUsers());
        }

        public ActionResult Edit(int id)
        {
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
            _userRepository.updateUser(user, role_id);
            return RedirectToAction("Index");
        }

        public ActionResult Add()
        {
            SelectList sl = new SelectList(_userRepository.getAllRoles(), "RoleId", "Name");
            ViewBag.RoleSelect = sl;

            return View();
        }

        [HttpPost]
        public ActionResult Add(User user, int role_id)
        {
            _userRepository.addUser(user, role_id);

            return RedirectToAction("Index");
        }
    }
}
