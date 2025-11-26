// Validación Bootstrap
(function () {
  'use strict';
  var forms = document.querySelectorAll('.needs-validation');
  Array.prototype.slice.call(forms).forEach(function (form) {
    form.addEventListener('submit', function (event) {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add('was-validated');
    }, false);
  });
})();

// IMC automático y comparativas
function calcularIMC() {
    var peso = parseFloat(document.getElementById('peso').value);
    var estatura = parseFloat(document.getElementById('estatura').value);
    var imcBox = document.getElementById('imcBox');
    var imcValue = document.getElementById('imcValue');
    
    if (peso > 0 && estatura > 0) {
        var imc = peso / Math.pow(estatura / 100, 2);
        imc = Math.round(imc * 10) / 10;
        imcValue.textContent = imc;
        imcBox.classList.remove('imc-ok', 'imc-warning', 'imc-danger');
        if (imc < 18.5) imcBox.classList.add('imc-warning');
        else if (imc < 25) imcBox.classList.add('imc-ok');
        else imcBox.classList.add('imc-danger');
        
        // Mostrar comparativa IMC si hay diagnóstico anterior
        if (diagnosticoAnterior && diagnosticoAnterior.peso && diagnosticoAnterior.estatura) {
            var imcAnterior = diagnosticoAnterior.peso / Math.pow(diagnosticoAnterior.estatura / 100, 2);
            imcAnterior = Math.round(imcAnterior * 10) / 10;
            mostrarComparativa('imcComparativa', imc, imcAnterior, 'IMC');
        }
    } else {
        imcValue.textContent = '--';
        imcBox.classList.remove('imc-ok', 'imc-warning', 'imc-danger');
    }
}

// Función para mostrar comparativas con mensajes descriptivos
function mostrarComparativa(elementId, valorActual, valorAnterior, etiqueta) {
    var elemento = document.getElementById(elementId);
    if (!elemento) return;
    
    var diferencia = valorActual - valorAnterior;
    var porcentaje = ((diferencia / valorAnterior) * 100).toFixed(1);
    var mensajeDescriptivo = '';
    var icono = '';

    // Determinar el mensaje según el tipo de medida
    if (Math.abs(diferencia) < 0.1) {
        // Sin cambios
        icono = '⚪';
        if (etiqueta === 'IMC') {
            mensajeDescriptivo = 'Tu IMC se mantiene igual';
        } else if (etiqueta === 'Peso') {
            mensajeDescriptivo = 'Tu peso se mantiene estable';
        } else if (etiqueta === 'Estatura') {
            mensajeDescriptivo = 'Tu estatura es la misma';
        }
        elemento.innerHTML = icono + ' ' + mensajeDescriptivo + ' <small>(Sin cambios)</small>';
        elemento.className = 'comparativa-badge sin-cambio';
        elemento.style.display = 'block';

    } else if (diferencia > 0) {
        // Incremento
        icono = '📈';
        if (etiqueta === 'IMC') {
            mensajeDescriptivo = 'Tu IMC aumentó';
        } else if (etiqueta === 'Peso') {
            mensajeDescriptivo = '¡Ganaste peso!';
        } else if (etiqueta === 'Estatura') {
            mensajeDescriptivo = 'Tu estatura aumentó';
        }
        elemento.innerHTML = icono + ' ' + mensajeDescriptivo + ' <small>(+' + diferencia.toFixed(1) + ' | +' + Math.abs(porcentaje) + '%)</small>';
        elemento.className = 'comparativa-badge incremento';
        elemento.style.display = 'block';

    } else {
        // Decremento
        icono = '📉';
        if (etiqueta === 'IMC') {
            mensajeDescriptivo = 'Tu IMC disminuyó';
        } else if (etiqueta === 'Peso') {
            mensajeDescriptivo = '¡Perdiste peso!';
        } else if (etiqueta === 'Estatura') {
            mensajeDescriptivo = 'Tu estatura disminuyó';
        }
        elemento.innerHTML = icono + ' ' + mensajeDescriptivo + ' <small>(' + diferencia.toFixed(1) + ' | ' + porcentaje + '%)</small>';
        elemento.className = 'comparativa-badge decremento';
        elemento.style.display = 'block';
    }
}

// Comparativa para peso
function compararPeso() {
    var peso = parseFloat(document.getElementById('peso').value);
    if (diagnosticoAnterior && diagnosticoAnterior.peso && peso > 0) {
        mostrarComparativa('pesoComparativa', peso, diagnosticoAnterior.peso, 'Peso');
    }
}

// Comparativa para estatura
function compararEstatura() {
    var estatura = parseFloat(document.getElementById('estatura').value);
    if (diagnosticoAnterior && diagnosticoAnterior.estatura && estatura > 0) {
        mostrarComparativa('estaturaComparativa', estatura, diagnosticoAnterior.estatura, 'Estatura');
    }
}

// Comparativa para nivel de experiencia
function compararNivelExperiencia() {
    var nivelActual = document.getElementById('nivelExperiencia').value;
    var elemento = document.getElementById('nivelExperienciaComparativa');
    
    if (!diagnosticoAnterior || !diagnosticoAnterior.nivelExperiencia || !nivelActual) {
        elemento.style.display = 'none';
        return;
    }
    
    var niveles = {
        'PRINCIPIANTE': 1,
        'INTERMEDIO': 2,
        'AVANZADO': 3
    };
    
    var nivelAnteriorNum = niveles[diagnosticoAnterior.nivelExperiencia];
    var nivelActualNum = niveles[nivelActual];
    
    if (nivelActualNum > nivelAnteriorNum) {
        elemento.innerHTML = '🎉 ¡Subiste de nivel!';
        elemento.className = 'comparativa-badge nivel-up';
        elemento.style.display = 'block';
    } else if (nivelActualNum === nivelAnteriorNum) {
        elemento.innerHTML = '⚪ Mismo nivel';
        elemento.className = 'comparativa-badge sin-cambio';
        elemento.style.display = 'block';
    } else {
        elemento.style.display = 'none';
    }
}

// Event listeners
document.getElementById('peso').addEventListener('input', function() {
    calcularIMC();
    compararPeso();
});

document.getElementById('estatura').addEventListener('input', function() {
    calcularIMC();
    compararEstatura();
});

document.getElementById('nivelExperiencia').addEventListener('change', compararNivelExperiencia);

// Ejecutar al cargar
document.addEventListener('DOMContentLoaded', function() {
    calcularIMC();
    compararPeso();
    compararEstatura();
    compararNivelExperiencia();
});


