﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace HOST_Admin.Models.ViewModel
{
    public class FormViewModel
    {
        public Form Form {get; set;}
        public List<QuestionViewModel> Questions {get; set;}

    }
}