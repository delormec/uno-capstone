using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using OST_Admin.Models.Repository;

namespace OST_Admin.Controllers
{
    public class HomeController : Controller
    {

        private readonly IFormRepository _formRepository;


        public HomeController(IFormRepository formRepository)
        {
            _formRepository = formRepository;
        }


        public ActionResult Index()
        {

            _formRepository.testy();

            return View();
        }
    }
}