// JavaScript para la página de registro de SABI

document.addEventListener('DOMContentLoaded', function() {
    // Elementos del formulario
    const form = document.querySelector('.registro-form');
    const nombreInput = document.getElementById('nombre');
    const apellidoInput = document.getElementById('apellido');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('contraseña');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const generoSelect = document.getElementById('genero');
    const fechaNacimientoInput = document.getElementById('fechaNacimiento');
    const departamentoSelect = document.getElementById('departamento');
    const ciudadSelect = document.getElementById('ciudad');
    const tipoDocumentoSelect = document.getElementById('tipoDocumento');
    const numeroDocumentoInput = document.getElementById('numeroDocumento');
    const terminosCheckbox = document.getElementById('terminos');
    const submitButton = document.querySelector('.registro-button');

    // Configurar ciudades por departamento
    const ciudadesPorDepartamento = {
        'ANTIOQUIA': ['Medellín', 'Bello', 'Itagüí', 'Envigado', 'Apartadó'],
        'CUNDINAMARCA': ['Bogotá', 'Soacha', 'Girardot', 'Zipaquirá', 'Facatativá'],
        'BOGOTA': ['Bogotá D.C'],
        'VALLE_DEL_CAUCA': ['Cali', 'Palmira', 'Buenaventura', 'Tuluá', 'Cartago'],
        'ATLANTICO': ['Barranquilla', 'Soledad', 'Malambo', 'Sabanalarga', 'Galapa'],
        'BOLIVAR': ['Cartagena', 'Magangué', 'Turbaco', 'Arjona', 'El Carmen de Bolívar'],
        'SANTANDER': ['Bucaramanga', 'Floridablanca', 'Girón', 'Piedecuesta', 'Barrancabermeja'],
        'OTRO': ['Otra ciudad']
    };

    // Actualizar ciudades cuando cambie el departamento
    departamentoSelect.addEventListener('change', function() {
        const departamento = this.value;
        ciudadSelect.innerHTML = '<option value="">Selecciona una ciudad</option>';
        if (departamento && ciudadesPorDepartamento[departamento]) {
            ciudadesPorDepartamento[departamento].forEach(ciudad => {
                const option = document.createElement('option');
                option.value = ciudad.toUpperCase().replace(/\s+/g, '_');
                option.textContent = ciudad;
                ciudadSelect.appendChild(option);
            });
            // Desplegar automáticamente el select de ciudad
            ciudadSelect.size = ciudadesPorDepartamento[departamento].length + 1;
            ciudadSelect.focus();
            setTimeout(() => { ciudadSelect.size = 0; }, 1200); // Vuelve al modo normal después de 1.2s
        }
    });

    // Validaciones en tiempo real
    nombreInput.addEventListener('input', function() {
        validateNombre(this);
    });

    apellidoInput.addEventListener('input', function() {
        validateApellido(this);
    });

    emailInput.addEventListener('input', function() {
        validateEmail(this);
    });

    passwordInput.addEventListener('input', function() {
        validatePassword(this);
        if (confirmPasswordInput.value) {
            validateConfirmPassword(confirmPasswordInput);
        }
    });

    confirmPasswordInput.addEventListener('input', function() {
        validateConfirmPassword(this);
    });

    generoSelect.addEventListener('change', function() {
        validateGenero(this);
    });

    fechaNacimientoInput.addEventListener('change', function() {
        validateFechaNacimiento(this);
    });

    departamentoSelect.addEventListener('change', function() {
        validateDepartamento(this);
    });

    ciudadSelect.addEventListener('change', function() {
        validateCiudad(this);
    });

    tipoDocumentoSelect.addEventListener('change', function() {
        validateTipoDocumento(this);
    });

    numeroDocumentoInput.addEventListener('input', function() {
        validateNumeroDocumento(this);
    });

    terminosCheckbox.addEventListener('change', function() {
        validateTerminos(this);
    });

    // Manejar el envío del formulario
    form.addEventListener('submit', function(e) {
        if (!validateForm()) {
            e.preventDefault();
            return false;
        }

        showLoading();
    });

    // Funciones de validación
    function validateNombre(input) {
        const isValid = input.value.trim().length >= 2;
        setInputState(input, isValid);
        return isValid;
    }

    function validateApellido(input) {
        const isValid = input.value.trim().length >= 2;
        setInputState(input, isValid);
        return isValid;
    }

    function validateEmail(input) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const isValid = emailRegex.test(input.value);
        setInputState(input, isValid);
        return isValid;
    }

    function validatePassword(input) {
        const isValid = input.value.length >= 6;
        setInputState(input, isValid);

        if (input.value.length > 0) {
            showPasswordStrength(input.value);
        }

        return isValid;
    }

    function validateConfirmPassword(input) {
        const isValid = input.value === passwordInput.value && input.value.length > 0;
        setInputState(input, isValid);
        return isValid;
    }

    function validateGenero(select) {
        const isValid = select.value !== '';
        setInputState(select, isValid);
        return isValid;
    }

    function validateFechaNacimiento(input) {
        if (!input.value) {
            setInputState(input, false);
            return false;
        }

        const fecha = new Date(input.value);
        const hoy = new Date();
        const edad = hoy.getFullYear() - fecha.getFullYear();
        const isValid = edad >= 14 && edad <= 100;

        setInputState(input, isValid);
        return isValid;
    }

    function validateDepartamento(select) {
        const isValid = select.value !== '';
        setInputState(select, isValid);
        return isValid;
    }

    function validateCiudad(select) {
        const isValid = select.value !== '';
        setInputState(select, isValid);
        return isValid;
    }

    function validateTipoDocumento(select) {
        const isValid = select.value !== '';
        setInputState(select, isValid);
        return isValid;
    }

    function validateNumeroDocumento(input) {
        const isValid = input.value.trim().length >= 6;
        setInputState(input, isValid);
        return isValid;
    }

    function validateTerminos(checkbox) {
        const isValid = checkbox.checked;
        const container = checkbox.closest('.checkbox-container');

        if (isValid) {
            container.style.color = '#10b981';
        } else {
            container.style.color = '#ef4444';
        }

        return isValid;
    }

    // Funciones auxiliares
    function setInputState(input, isValid) {
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
    }

    function validateForm() {
        const nombreValid = validateNombre(nombreInput);
        const apellidoValid = validateApellido(apellidoInput);
        const emailValid = validateEmail(emailInput);
        const passwordValid = validatePassword(passwordInput);
        const confirmPasswordValid = validateConfirmPassword(confirmPasswordInput);
        const generoValid = validateGenero(generoSelect);
        const fechaNacimientoValid = validateFechaNacimiento(fechaNacimientoInput);
        const departamentoValid = validateDepartamento(departamentoSelect);
        const ciudadValid = validateCiudad(ciudadSelect);
        const tipoDocumentoValid = validateTipoDocumento(tipoDocumentoSelect);
        const numeroDocumentoValid = validateNumeroDocumento(numeroDocumentoInput);
        const terminosValid = validateTerminos(terminosCheckbox);

        return nombreValid && apellidoValid && emailValid && passwordValid &&
               confirmPasswordValid && generoValid && fechaNacimientoValid &&
               departamentoValid && ciudadValid && tipoDocumentoValid &&
               numeroDocumentoValid && terminosValid;
    }

    function showLoading() {
        submitButton.innerHTML = 'Registrando...';
        submitButton.disabled = true;
        submitButton.style.opacity = '0.7';
    }

    function showPasswordStrength(password) {
        // Remover indicador anterior si existe
        const existingIndicator = document.querySelector('.password-strength');
        if (existingIndicator) {
            existingIndicator.remove();
        }

        if (password.length === 0) return;

        let strength = 0;
        let color = '#ef4444';
        let text = 'Muy débil';

        // Criterios de fortaleza
        if (password.length >= 6) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;

        switch (strength) {
            case 1:
            case 2:
                color = '#ef4444';
                text = 'Débil';
                break;
            case 3:
                color = '#f59e0b';
                text = 'Regular';
                break;
            case 4:
                color = '#10b981';
                text = 'Fuerte';
                break;
            case 5:
                color = '#059669';
                text = 'Muy fuerte';
                break;
        }

        const indicator = document.createElement('div');
        indicator.className = 'password-strength';
        indicator.style.cssText = `
            font-size: 0.8rem;
            color: ${color};
            margin-top: 5px;
            font-weight: 600;
        `;
        indicator.textContent = `Fortaleza: ${text}`;

        passwordInput.parentElement.appendChild(indicator);
    }
});
