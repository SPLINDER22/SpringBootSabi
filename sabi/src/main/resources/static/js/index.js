// Animaciones interactivas para la página de inicio
document.addEventListener('DOMContentLoaded', function() {
    
    // Animaciones para los separadores
    function initDividerAnimations() {
        const dividers = document.querySelectorAll('.section-divider');
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                    
                    // Animación especial para decoraciones
                    const dots = entry.target.querySelectorAll('.decoration-dot');
                    dots.forEach((dot, index) => {
                        setTimeout(() => {
                            dot.style.animation = 'dotPulse 1.5s infinite';
                            dot.style.animationDelay = `${index * 0.3}s`;
                        }, 200);
                    });
                    
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.3 });

        dividers.forEach(divider => {
            divider.style.opacity = '0';
            divider.style.transform = 'translateY(20px)';
            divider.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
            observer.observe(divider);
        });
    }

    // Efectos especiales para los botones principales
    function initEnhancedButtons() {
        // Buscar tanto los botones antiguos como los nuevos XL
        const primaryBtn = document.querySelector('.btn-primary-large, .btn-register-xl');
        const secondaryBtn = document.querySelector('.btn-secondary-large, .btn-login-xl');
        
        // Todos los botones grandes para efectos generales
        const allButtons = document.querySelectorAll('.btn-primary-large, .btn-secondary-large, .btn-register-xl, .btn-login-xl');

        if (primaryBtn) {
            // Efecto de partículas en el botón principal
            primaryBtn.addEventListener('mouseenter', () => {
                primaryBtn.style.transform = 'translateY(-8px) scale(1.03)';
                primaryBtn.style.boxShadow = '0 20px 40px rgba(42, 122, 228, 0.3)';
                createButtonParticles(primaryBtn);
            });

            primaryBtn.addEventListener('mouseleave', () => {
                primaryBtn.style.transform = 'translateY(0) scale(1)';
                primaryBtn.style.boxShadow = '0 8px 20px rgba(42, 122, 228, 0.15)';
            });

            // Efecto de pulso cada 3 segundos para llamar la atención
            setInterval(() => {
                if (!primaryBtn.matches(':hover')) {
                    primaryBtn.style.animation = 'pulse 0.6s ease';
                    setTimeout(() => {
                        primaryBtn.style.animation = 'gradientShift 8s ease-in-out infinite';
                    }, 600);
                }
            }, 3000);
        }

        if (secondaryBtn) {
            secondaryBtn.addEventListener('mouseenter', () => {
                secondaryBtn.style.transform = 'translateY(-6px) scale(1.02)';
                secondaryBtn.style.boxShadow = '0 15px 30px rgba(108, 117, 125, 0.2)';
            });

            secondaryBtn.addEventListener('mouseleave', () => {
                secondaryBtn.style.transform = 'translateY(0) scale(1)';
                secondaryBtn.style.boxShadow = '0 4px 15px rgba(108, 117, 125, 0.1)';
            });
        }
        
        // Efectos táctiles para móviles en todos los botones
        allButtons.forEach(btn => {
            btn.addEventListener('touchstart', function(e) {
                this.style.transform = 'translateY(-2px) scale(0.98)';
            }, {passive: true});
            
            btn.addEventListener('touchend', function(e) {
                this.style.transform = 'translateY(0) scale(1)';
            }, {passive: true});
            
            // Efecto de click para retroalimentación
            btn.addEventListener('click', function() {
                this.style.transform = 'translateY(-1px) scale(0.98)';
                setTimeout(() => {
                    this.style.transform = 'translateY(0) scale(1)';
                }, 100);
            });
        });
    }

    // Crear pequeñas partículas de éxito para el botón principal
    function createButtonParticles(button) {
        const particles = ['✨', '🚀', '⭐', '💫'];
        const rect = button.getBoundingClientRect();

        for (let i = 0; i < 3; i++) {
            setTimeout(() => {
                const particle = document.createElement('span');
                particle.textContent = particles[Math.floor(Math.random() * particles.length)];
                particle.style.position = 'fixed';
                particle.style.left = (rect.left + Math.random() * rect.width) + 'px';
                particle.style.top = rect.top + 'px';
                particle.style.pointerEvents = 'none';
                particle.style.zIndex = '9999';
                particle.style.fontSize = '1.2rem';
                particle.style.opacity = '1';
                particle.style.transition = 'all 1s ease-out';

                document.body.appendChild(particle);

                // Animar hacia arriba y desvanecer
                setTimeout(() => {
                    particle.style.transform = 'translateY(-50px)';
                    particle.style.opacity = '0';
                }, 10);

                // Remover después de la animación
                setTimeout(() => {
                    document.body.removeChild(particle);
                }, 1100);
            }, i * 100);
        }
    }

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

    // Animación de contadores en las tarjetas de características
    function animateFeatureCards() {
        const featureCards = document.querySelectorAll('.feature-card');
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry, index) => {
                if (entry.isIntersecting) {
                    // Añadir animación escalonada
                    setTimeout(() => {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateY(0)';
                    }, index * 100);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1 });

        featureCards.forEach(card => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            observer.observe(card);
        });
    }

    // Animación de los pasos del demo
    function animateDemoSteps() {
        const demoSteps = document.querySelectorAll('.demo-step');
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry, index) => {
                if (entry.isIntersecting) {
                    setTimeout(() => {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateY(0)';
                        
                        // Añadir efecto de pulse al número
                        const stepNumber = entry.target.querySelector('.step-number');
                        if (stepNumber) {
                            stepNumber.style.animation = 'pulse 0.6s ease';
                        }
                    }, index * 200);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.3 });

        demoSteps.forEach(step => {
            step.style.opacity = '0';
            step.style.transform = 'translateY(30px)';
            step.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
            observer.observe(step);
        });
    }

    // Efecto de hover para las tarjetas de caso de uso
    function initCaseCardInteractions() {
        const caseCards = document.querySelectorAll('.case-card');
        
        caseCards.forEach(card => {
            card.addEventListener('mouseenter', () => {
                const icon = card.querySelector('.case-icon');
                if (icon) {
                    icon.style.transform = 'scale(1.2) rotate(5deg)';
                    icon.style.transition = 'transform 0.3s ease';
                }
            });
            
            card.addEventListener('mouseleave', () => {
                const icon = card.querySelector('.case-icon');
                if (icon) {
                    icon.style.transform = 'scale(1) rotate(0deg)';
                }
            });
        });
    }

    // Animación de los dashboards reales
    function initDashboardShowcase() {
        const dashboardCards = document.querySelectorAll('.dashboard-card');
        
        dashboardCards.forEach((card, index) => {
            card.addEventListener('mouseenter', () => {
                const screenshot = card.querySelector('.dash-screenshot');
                const badge = card.querySelector('.dashboard-badge');
                if (screenshot) {
                    screenshot.style.transform = 'scale(1.05)';
                }
                if (badge) {
                    badge.style.transform = 'scale(1.1)';
                    badge.style.transition = 'transform 0.3s ease';
                }
            });
            
            card.addEventListener('mouseleave', () => {
                const screenshot = card.querySelector('.dash-screenshot');
                const badge = card.querySelector('.dashboard-badge');
                if (screenshot) {
                    screenshot.style.transform = 'scale(1)';
                }
                if (badge) {
                    badge.style.transform = 'scale(1)';
                }
            });
        });

        // Animación de entrada para los dashboards
        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry, index) => {
                if (entry.isIntersecting) {
                    setTimeout(() => {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateY(0)';
                    }, index * 200);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.2 });

        dashboardCards.forEach(card => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(30px)';
            card.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
            observer.observe(card);
        });
    }

    // Efecto de escritura para las características de los dashboards
    function typeWriterEffect() {
        const featureTags = document.querySelectorAll('.feature-tag');
        
        featureTags.forEach((tag, index) => {
            const originalText = tag.textContent;
            tag.textContent = '';
            tag.style.opacity = '0';
            
            setTimeout(() => {
                tag.style.opacity = '1';
                let i = 0;
                const typeInterval = setInterval(() => {
                    tag.textContent = originalText.slice(0, i + 1);
                    i++;
                    if (i >= originalText.length) {
                        clearInterval(typeInterval);
                    }
                }, 30);
            }, index * 200 + 500);
        });
    }

    // Inicializar todas las animaciones
    initDividerAnimations();
    initEnhancedButtons();
    animateFeatureCards();
    animateDemoSteps();
    initCaseCardInteractions();
    initDashboardShowcase();
    
    // Iniciar el efecto de escritura cuando se hace visible la sección de preview
    const demoSection = document.querySelector('.demo-section');
    if (demoSection) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    setTimeout(typeWriterEffect, 500);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.5 });
        
        observer.observe(demoSection);
    }

    // Smooth scrolling para enlaces internos
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

});