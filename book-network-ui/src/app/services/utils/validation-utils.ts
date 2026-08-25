export class ValidationUtils {

    private static readonly EMAIL_REGEX=/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    /**
   * Validates if a string matches a standard email format.
   */
    static isValidEmail(email: string): boolean {
        return this.EMAIL_REGEX.test(email);
    }
}