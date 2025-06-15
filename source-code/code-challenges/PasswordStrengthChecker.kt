class PasswordStrengthChecker {
    
    enum class PasswordStrength {
        WEAK, 
        MEDIUM, 
        STRONG
    }
    
    fun checkStrength(password: String): PasswordStrength {
        if (password.length < 8) {
            return PasswordStrength.WEAK
        }
        
        val hasUppercase = password.matches(Regex(".*[A-Z].*"))
        val hasLowercase = password.matches(Regex(".*[a-z].*"))
        val hasDigit = password.matches(Regex(".*\\d.*"))
        val hasSpecialChar = password.matches(Regex(".*[^A-Za-z0-9].*"))
        
        val typesCount = listOf(hasUppercase, hasLowercase, hasDigit, hasSpecialChar)
            .count { it }
        
        return when {
            password.length >= 10 && typesCount == 4 -> PasswordStrength.STRONG
            typesCount >= 2 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.WEAK
        }
    }
    
    fun getImprovement(password: String): String {
        val strength = checkStrength(password)
        
        if (strength == PasswordStrength.STRONG) {
            return "Password strength is excellent!"
        }
        
        val suggestions = mutableListOf<String>()
        
        if (password.length < 8) {
            suggestions.add("Use at least 8 characters")
        }
        
        if (password.length < 10) {
            suggestions.add("Consider using at least 10 characters for stronger security")
        }
        
        if (!password.matches(Regex(".*[A-Z].*"))) {
            suggestions.add("Add uppercase letters (A-Z)")
        }
        
        if (!password.matches(Regex(".*[a-z].*"))) {
            suggestions.add("Add lowercase letters (a-z)")
        }
        
        if (!password.matches(Regex(".*\\d.*"))) {
            suggestions.add("Add numbers (0-9)")
        }
        
        if (!password.matches(Regex(".*[^A-Za-z0-9].*"))) {
            suggestions.add("Add special characters (e.g., @, #, $, !)")
        }
        
        return suggestions.joinToString("\n- ", "To improve your password:\n- ")
    }
}