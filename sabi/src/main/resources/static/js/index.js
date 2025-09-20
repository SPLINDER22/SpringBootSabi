// Animación extra opcional para las tarjetas
const cards = document.querySelectorAll('.action-card');
cards.forEach(card => {
    card.addEventListener('mouseenter', () => {
        card.classList.add('hovered');
    });
    card.addEventListener('mouseleave', () => {
        card.classList.remove('hovered');
    });
});