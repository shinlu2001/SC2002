### 5.3. Lessons Learned

Throughout the development of the BTO Management System, our team gained valuable insights into effective coding practices that can be applied to future projects:

1. **Plan Before Implementation**:
   Investing time in detailed design planning before writing code significantly reduced rework. Creating UML diagrams helped us visualize complex relationships and anticipate potential issues early in the development process.

2. **Follow Design Principles Consistently**:
   Adhering to established design principles like SOLID improved code quality and maintainability. For example, applying the Single Responsibility Principle made our codebase more modular and easier to test, while the Open-Closed Principle facilitated adding new features without modifying existing code.

3. **Implement Proper Input Validation**:
   We learned that robust validation at all levels (UI, service, data storage) is critical for system stability. Never trust user input, and validate data consistently through the application to prevent cascading errors.

4. **Structure Error Handling Thoughtfully**:
   Using custom exceptions with meaningful messages improved debugging efficiency and user experience. Our approach of handling exceptions at appropriate levels rather than extensive try-catch blocks made error identification more straightforward.

5. **Separate Concerns Effectively**:
   The Boundary-Control-Entity architecture provided clear separation of responsibilities, making the codebase more navigable and testable. When adding new features, this separation allowed us to focus on specific components without affecting the entire system.

6. **Integrate Testing Throughout Development**:
   Testing regularly during development rather than at the end helped identify issues early, reducing debugging time and preventing regression bugs. Each feature implementation was followed by testing the happy path, boundary cases, and error scenarios.

7. **Utilize Version Control Strategically**:
   Using Git effectively with focused commits, descriptive messages, and feature branches made collaboration smoother. Our practice of making small, atomic commits with meaningful descriptions facilitated easier code reviews and issue tracking.

8. **Choose Composition Over Inheritance When Appropriate**:
   We discovered that inheritance relationships should be used sparingly. In many cases, composition offered more flexibility without the constraints of the "is-a" relationship, particularly when modeling complex business relationships.

9. **Design for the User, Not the Code**:
   Focusing on user workflows rather than technical implementation details led to a more intuitive system. Regularly considering how end users would interact with our application helped prioritize features and improve usability.

10. **Document Consistently and Thoroughly**:
    Consistent documentation practices, including clear Javadoc comments, descriptive method names, and regular code annotations, accelerated onboarding and improved team communication. Well-documented code proved especially valuable when revisiting complex components.

11. **Use Interfaces for Flexibility**:
    Interface-based design enabled polymorphic behavior and facilitated easier testing through dependency injection. Interfaces like StaffControllerInterface allowed us to share code between different controllers while maintaining specific business logic where needed.

12. **Implement Defensive Coding Practices**:
    Null checks, proper exception handling, and input validation throughout the codebase prevented unexpected crashes and improved system robustness. Anticipating potential failure points rather than assuming "happy path" execution made our system more resilient.