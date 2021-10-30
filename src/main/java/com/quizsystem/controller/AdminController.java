package com.quizsystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizsystem.dto.QuestionDto;
import com.quizsystem.model.Course;
import com.quizsystem.model.Question;
import com.quizsystem.service.CourseService;
import com.quizsystem.service.QuestionService;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

	@Autowired
	private CourseService courseService;

	@Autowired
	private QuestionService questionService;

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String adminhomePage() {
		return "adminHome";
	}

	// Cac ham thao tac cho course
	// Tra ve list Course
	public String coursePage(Model model) {
		return "course";
	}

	@RequestMapping(value = "/course/add", method = RequestMethod.GET)
	public String addCoursePage(Model model) {
		Course course = new Course();
		model.addAttribute("course", course);
		return "addCourse";
	}

	//Name: QuocHUy
	//Date: 17/09/2021
	//Task: Modified function show notification for admin when added successful
	@RequestMapping(value = "/course/save", method = RequestMethod.POST)
	public String saveCourse(@ModelAttribute(name = "course") Course course, BindingResult result,
			RedirectAttributes redirectAttributes) {

		courseService.saveCourse(course);
		
		try {
			courseService.saveCourse(course);
			redirectAttributes.addFlashAttribute("message", "Success");
			redirectAttributes.addFlashAttribute("alertClass", "alert-success");
			return "redirect:/admin/course";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Failed");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			return "redirect:/admin/course";
		}
	}

	// show the list of course
	@RequestMapping(value = "/course", method = RequestMethod.GET)
	public String viewListCourse(Model model) {
		List<Course> listCourses = courseService.getAllCourse();
		model.addAttribute("listCourses", listCourses);
		return "listCourse";
	}

	@RequestMapping(value = "/course/update/{id}", method = RequestMethod.GET)
	public String updateCourse(@PathVariable(value = "id") int id, Model model) {

		// get course from the service
		Course course = courseService.getCourseById(id);

		model.addAttribute("Course", course);
		return "updateCourse";
	}

	@RequestMapping(value = "/course/delete/{id}", method = RequestMethod.GET)
	public String deleteCourse(@PathVariable(value = "id") int id) {
		
		//Get all questions belongs to course
		List<Question> questions = new ArrayList<>();
		questions = questionService.getAllQuestionByCourseId(id);
		
		if(questions.size() > 0) {
			for (Question question : questions) {
				int questionId = question.getId();
				questionService.deleteQuestionById(questionId);
			}
		}
		
		this.courseService.deleteCourseById(id);
		return "redirect:/admin/course";
	}

	// Cac ham thao tac cho question
	// Tra ve page add question
	@RequestMapping(value = "/question/add", method = RequestMethod.GET)
	public String addQuestionPage(Model model) {
		Question question = new Question();
		model.addAttribute("question", question);

		List<Course> courses = courseService.getAllCourse();
		model.addAttribute("courses", courses);
		return "addQuestion";
	}

	@RequestMapping(value = "/question/add", method = RequestMethod.POST)
	public String saveQuestion(@ModelAttribute(name = "question") Question question) {
		Course course = new Course();
		course = courseService.getCourseById(question.getCourse().getId());

		question.setCourse(course);
		question.setChose(-1);

		questionService.saveQuestion(question);
		return "redirect:/admin/question";
	}

	@RequestMapping(value = "/question", method = RequestMethod.GET)
	public String getAllCourseForEditQuestion(Model model) {
		List<Course> courses = new ArrayList<>();
		courses = courseService.getAllCourse();
		model.addAttribute("courses", courses);
		return "listCourseQuestion";
	}
	
	
	// This method will show all questions belongs to course ID for admin to manage the question due to course
	@RequestMapping(value = "/question/list", method = RequestMethod.GET)
	public String getAllQuestionByCourseId(@RequestParam(name = "course_id") int course_id, Model model) {
		List<Question> questions = new ArrayList<>();
		questions = questionService.getAllQuestionByCourseId(course_id);

		Course course = new Course();
		course = courseService.getCourseById(course_id);
		model.addAttribute("questions", questions);
		model.addAttribute("course", course);
		return "listQuestionByCourse";
	}

	// delete information of question
	@RequestMapping(value = "/question/delete", method = RequestMethod.GET)
	public String deleteQuestion(@RequestParam(name = "question_id") int question_id,
			@RequestParam(name = "course_id") int course_id) {
		questionService.deleteQuestionById(question_id);
		return "redirect:/admin/question/list?course_id=" + course_id;
	}

	// Name: QuocHuyDev
	// Date: 14/09/2021
	// Task: Create update question functional for admin
	@RequestMapping(value = "/question/update/{id}", method = RequestMethod.GET)
	public String updateQuestionPage(@PathVariable(value = "id") int id, Model model) {
		Optional<Question> optionalQuestion = questionService.getOptionalQuestionById(id);
		if (optionalQuestion.isPresent()) {
			Question question = optionalQuestion.get();

			QuestionDto questionDto = new QuestionDto();

			questionDto.setId(question.getId());
			questionDto.setTitle(question.getTitle());
			questionDto.setCourseId(question.getCourse().getId());
			questionDto.setOptionA(question.getOptionA());
			questionDto.setOptionB(question.getOptionB());
			questionDto.setOptionC(question.getOptionC());
			questionDto.setOptionD(question.getOptionD());
			questionDto.setAnswer(question.getAnswer());
			questionDto.setChose(question.getChose());

			Course courseQuestion = new Course();
			courseQuestion = courseService.getCourseById(question.getCourse().getId());

			model.addAttribute("questionDto", questionDto);
			model.addAttribute("courseQuestion", courseQuestion);

			return "updateQuestion";
		}
		return "404";
	}

	@RequestMapping(value = "/question/update/save", method = RequestMethod.POST)
	public String updateQuestion(@ModelAttribute(name = "questionDto") QuestionDto questionDto) {
		Question question = new Question();

		question.setId(questionDto.getId());
		question.setTitle(questionDto.getTitle());
		question.setCourse(courseService.getCourseById(questionDto.getCourseId()));
		question.setOptionA(questionDto.getOptionA());
		question.setOptionB(questionDto.getOptionB());
		question.setOptionC(questionDto.getOptionC());
		question.setOptionD(questionDto.getOptionD());
		question.setAnswer(questionDto.getAnswer());
		question.setChose(questionDto.getChose());

		questionService.saveQuestion(question);

		int course_id = questionDto.getCourseId();
		return "redirect:/admin/question/list?course_id=" + course_id;
	}

	// End 14/09/2021
}
