using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

public class MyHTML
{
    /// <summary>
    /// Generates HTML for a question mark image with hover text.
    /// </summary>
    /// <param name="alt">The text to display on mouse hover</param>
    /// <returns>HTML for a question mark image with hover text</returns>
    public static HtmlString Help(string alt)
    {
        return new HtmlString(String.Format("<img src=\"/Content/images/question.png\" alt=\"{0}\" title=\"{1}\" />", alt, alt));
    }
    
}
