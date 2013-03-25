using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace HOST_Admin.Models.Repository
{
    public interface IFormRepository
    {
        IQueryable<Form> GetAll();
        Form getFormById(int form_id);
        //void updateForm(Form form);
        void updateForm(Form form, List<Question> question_list);
        void deleteFormById(int form_id);
        void addQuestion(int form_id, string question_type);
        int deleteQuestion(int question_id);
        ChoiceQuestion addOption(int question_id);
        int deleteOption(int option_id);
        Question getQuestionById(int question_id);
        void addForm(Form form);
    }
}