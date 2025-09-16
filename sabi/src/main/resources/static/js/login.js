// JavaScript para la página de login de SABI

document.addEventListener('DOMContentLoaded', function() {
    // Elementos del formulario
    const form = document.querySelector('.login-form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const loginButton = document.querySelector('.login-button');

    // Validación en tiempo real del email
    emailInput.addEventListener('input', function() {
        validateEmail(this);
    });

    // Validación en tiempo real de la contraseña
    passwordInput.addEventListener('input', function() {
        validatePassword(this);
    });

    // Manejar el envío del formulario
    form.addEventListener('submit', function(e) {
        if (!validateForm()) {
            e.preventDefault();
            return false;
        }

        // Mostrar loading en el botón
        showLoading();
    });

    // Función para validar email
    function validateEmail(input) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const isValid = emailRegex.test(input.value);

        if (input.value.length > 0) {
            if (isValid) {
                input.style.borderColor = '#10b981';
                input.style.background = '#f0fdf4';
            } else {
                input.style.borderColor = '#ef4444';
                input.style.background = '#fef2f2';
            }
        } else {
            input.style.borderColor = '#e5e7eb';
            input.style.background = '#f9fafb';
        }

        return isValid;
    }

    // Función para validar contraseña
    function validatePassword(input) {
        const isValid = input.value.length >= 6;

        if (input.value.length > 0) {
            if (isValid) {
                input.style.borderColor = '#10b981';
                input.style.background = '#f0fdf4';
            } else {
                input.style.borderColor = '#ef4444';
                input.style.background = '#fef2f2';
            }
        } else {
            input.style.borderColor = '#e5e7eb';
            input.style.background = '#f9fafb';
        }

        return isValid;
    }

    // Función para validar todo el formulario
    function validateForm() {
        const emailValid = validateEmail(emailInput);
        const passwordValid = validatePassword(passwordInput);

        return emailValid && passwordValid;
    }

    // Función para mostrar loading en el botón
    function showLoading() {
        loginButton.innerHTML = 'Iniciando sesión...';
        loginButton.disabled = true;
        loginButton.style.opacity = '0.7';
    }

    // Animación suave para los inputs
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.style.transform = 'translateY(-2px)';
        });

        input.addEventListener('blur', function() {
            this.parentElement.style.transform = 'translateY(0)';
        });
    });

    // Efecto de partículas en el fondo (opcional)
    createFloatingIcons();
});

// Función para crear iconos flotantes en el fondo
function createFloatingIcons() {
    const icons = ['💪', '🏃‍♂️', '🏋️‍♀️', '⏰', '📊', '🎯'];
    const container = document.querySelector('.logo-section');

    setInterval(() => {
        if (document.querySelectorAll('.floating-icon').length < 5) {
            const icon = document.createElement('div');
            icon.className = 'floating-icon';
            icon.innerHTML = icons[Math.floor(Math.random() * icons.length)];
            icon.style.cssText = `
                position: absolute;
                font-size: 2rem;
                opacity: 0.1;
                pointer-events: none;
                left: ${Math.random() * 100}%;
                top: 100%;
                animation: floatUp 8s linear infinite;
                z-index: 1;
            `;

            container.appendChild(icon);

            // Remover el icono después de la animación
            setTimeout(() => {
                if (icon.parentNode) {
                    icon.parentNode.removeChild(icon);
                }
            }, 8000);
        }
    }, 2000);
}

// CSS para la animación de iconos flotantes
const style = document.createElement('style');
style.textContent = `
    @keyframes floatUp {
        0% {
            transform: translateY(0) rotate(0deg);
            opacity: 0;
        }
        10% {
            opacity: 0.1;
        }
        90% {
            opacity: 0.1;
        }
        100% {
            transform: translateY(-600px) rotate(360deg);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
