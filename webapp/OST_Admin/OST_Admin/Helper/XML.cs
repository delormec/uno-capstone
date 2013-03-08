using OST_Admin.Models.Repository;
using OST_Admin.Models;
using System.Collections.Generic;

public class XML
{
    public XML()
    {}

    
    /// <summary>
    /// Generates XML for the form with the given ID
    /// </summary>
    /// <param name="formID">The ID of the form to convert to XML</param>
    /// <returns>The XML as a string</returns>
    public static string fromFormID(int formID)
    {
        IOSTDataContext _OSTDataContext = new OSTDataContext();
        FormRepository formRepository = new FormRepository(_OSTDataContext);

        // Pull out the form from the database
        Form form = formRepository.getFormById(formID);

        // Use the fromForm method
        return fromForm(form);
    }


    /// <summary>
    /// Generates XML for a given form.
    /// </summary>
    /// <param name="form">The form to convert to XML</param>
    /// <returns>The XML as a string</returns>
    public static string fromForm(Form form)
    {
        string xml;

        // Pull out the questions list from the form
        System.Data.Objects.DataClasses.EntityCollection<Question> questions = form.Questions;


        // Open the form and meta tags
        xml = "<form>\n\t<meta>\n";

        // Add the meta data to the XML string
        xml += string.Format("\t\t<template_id>{0}</template_id>\n", form.FormId);
        xml += string.Format("\t\t<name>{0}</name>\n", form.Name);
        xml += string.Format("\t\t<url>{0}</url>\n", form.URL);
        xml += string.Format("\t\t<listname>{0}</listname>\n", form.ListName);
        xml += string.Format("\t\t<keyfield>{0}</keyfield>\n", form.KeyField);
        xml += string.Format("\t\t<formgroup>{0}</formgroup>\n", form.Group);
        
        // Autoupdate
        if (form.AutoUpdate == null || form.AutoUpdate == false)
        {
            xml += string.Format("\t\t<autoupload>{0}</autoupload>\n", "false");
        }
        else
        {
            xml += string.Format("\t\t<autoupload>{0}</autoupload>\n", "true");
        }

        xml += string.Format("\t\t<datecreated>{0}</datecreated>\n", form.DateCreated.ToString());
        xml += string.Format("\t\t<description>{0}</description>\n", form.Description);


        // These two are filled in on the phone app
        xml += "\t\t<user></user>\n";
        xml += "\t\t<pass></pass>\n";


        // Close the meta tag
        xml += "\t</meta>\n\n";



        // Open the questions tag
        xml += "\t<questions>\n";

        // Go through each question and add it to the XML
        foreach (Question q in questions)
        {

            if (q is TextQuestion)
                xml += "\t\t<question class=\"TextQuestion\">\n";
            else if (q is ChoiceQuestion)
                xml += "\t\t<question class=\"ChoiceQuestion\">\n";
            else if (q is LikertScaleQuestion)
                xml += "\t\t<question class=\"LikertScaleQuestion\">\n";


            // Common question info
            xml += string.Format("\t\t\t<id>{0}</id>\n", q.QuestionId);
            xml += string.Format("\t\t\t<sortorder>{0}</sortorder>\n", q.SortOrder);
            xml += string.Format("\t\t\t<text>{0}</text>\n", q.Text);
            xml += string.Format("\t\t\t<helptext>{0}</helptext>\n", q.HelpText);
            xml += string.Format("\t\t\t<fieldname>{0}</fieldname>\n", q.FieldName);
            xml += string.Format("\t\t\t<fieldtype>{0}</fieldtype>\n", q.FieldType);


            // Text question info
            if (q is TextQuestion)
            {
                // No extra special info
            }


            // Multiple choice question info
            else if (q is ChoiceQuestion)
            {
                // Cast q as a ChoiceQuestion
                ChoiceQuestion question = (ChoiceQuestion)q;


                // Multiple select
                if (question.Multiple == true)
                {
                    xml += "\t\t\t<multipleselect>true</multipleselect>\n";
                }
                else
                {
                    xml += "\t\t\t<multipleselect>false</multipleselect>\n";
                }


                // Open options tag
                xml += "\n\t\t\t<options>\n";

                
                // Add each option to the question
                foreach (Option o in question.Options)
                {
                    xml += "\t\t\t\t<option>\n";
                    xml += string.Format("\t\t\t\t\t<id>{0}</id>\n", o.OptionId);
                    xml += string.Format("\t\t\t\t\t<sortorder>{0}</sortorder>\n", o.SortOrder);
                    xml += string.Format("\t\t\t\t\t<text>{0}</text>\n", o.Text);
                    xml += "\t\t\t\t</option>\n\n";
                }         


                // Other
                if (question.Other == true)
                {
                    xml += "\t\t\t\t<other>true</other>\n";
                }
                else
                {
                    xml += "\t\t\t\t<other>false</other>\n";
                }

                // Close options tag
                xml += "\t\t\t</options>\n";
            }

            // Likert scale question info
            else if (q is LikertScaleQuestion)
            {
                // Cast q as a LikertScaleQuestion
                LikertScaleQuestion question = (LikertScaleQuestion)q;

                // Steps
                xml += string.Format("\t\t\t<steps>{0}</steps>\n", question.Steps);

                // Open labels tag
                xml += "\n\t\t\t<labels>\n";

                // Add each label to the question
                foreach (Label l in question.Labels)
                {
                    xml += "\t\t\t\t<label>\n";
                    xml += string.Format("\t\t\t\t\t<id>{0}</id>\n", l.LabelId);
                    xml += string.Format("\t\t\t\t\t<text>{0}</text>\n", l.Text);
                    xml += string.Format("\t\t\t\t\t<range>{0}</range>\n", l.Range);
                    xml += "\t\t\t\t</label>\n\n";
                }

                // Close labels tag
                xml += "\t\t\t</labels>\n";
            }


            // Filled in on the phone app
            xml += "\n\t\t\t<answer></answer>\n\n";  

            // Close question tag
            
            xml += "\t\t</question>\n\n\n";
        }

        // Close questions tag and form tag
        xml += "\t</questions>\n</form>";


        // Return the xml as a string
        return xml;
    }
}