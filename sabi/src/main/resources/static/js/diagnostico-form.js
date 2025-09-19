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
// IMC automático
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
    } else {
        imcValue.textContent = '--';
        imcBox.classList.remove('imc-ok', 'imc-warning', 'imc-danger');
    }
}
document.getElementById('peso').addEventListener('input', calcularIMC);
document.getElementById('estatura').addEventListener('input', calcularIMC);
document.addEventListener('DOMContentLoaded', calcularIMC);

