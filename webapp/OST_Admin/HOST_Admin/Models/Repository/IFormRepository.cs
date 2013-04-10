using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using HOST_Admin.Models.ViewModel;

namespace HOST_Admin.Models.Repository
{
    /// <summary>
    /// Interface for FormRepository.
    /// This interface is required to support the use of dependency injection.
    /// Dependecy injection allows the creation of Controller constructors that accept this interface.
    /// At runtime the constructors are intercepted and properly mapped to the repositories that implement this interface.
    /// The benificial side effect, and reason for this, is that this opens the controllers up for unit testing by stubbing/mocking.
    /// </summary>
    public interface IFormRepository
    {
        IQueryable<Form> getAll();
        Form getFormById(int form_id);
        void updateForm(Form form, List<Question> question_list);
        void deleteFormById(int form_id);
        void addQuestion(int form_id, string question_type);
        int deleteQuestion(int question_id);
        void addOption(int question_id);
        int deleteOption(int option_id);
        Question getQuestionById(int question_id);
        void addForm(Form form);
        Form copyForm(Form old_form, int user_id);
        void changeQuestionPosition(int question_id, int start_position, int end_position);
        Form createFormFromSharePoint(List<SharePointSOAPResponseViewModel> spsrvmList, int user_id);
    }
}