﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace OST_Admin.Models.ViewModel
{
    public class QuestionViewModel
    {
        string ModelType { get; set; }
        int QuestionId { get; set; }
        string Text { get; set; }
        string HelpText { get; set; }
        string FieldName { get; set; }
        string FieldType { get; set; }
        int SortOrder { get; set; }
        bool Other { get; set; }
        int Steps { get; set; }

        List<Option> Options {get; set;}
        List<Label> Label { get; set; }
    }
}