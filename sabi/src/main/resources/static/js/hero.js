// Script para animar el hero: parallax ligero, interacción y reveal
(function(){
    'use strict';

    // Detectar si el dispositivo es táctil o pointer coarse
    function isTouchDevice(){
        try{
            return ('ontouchstart' in window) || navigator.maxTouchPoints > 0 || window.matchMedia('(pointer: coarse)').matches;
        }catch(e){
            return false;
        }
    }

    // Parallax simple para el blob y el grupo SVG según el movimiento del ratón
    function initParallax(){
        const hero = document.querySelector('.hero');
        const svgGroup = document.querySelector('.hero-svg-group');
        if(!hero || !svgGroup) return;

        // no añadimos parallax en pantallas táctiles o pequeñas
        if(isTouchDevice() || window.innerWidth < 900){
            svgGroup.style.transform = '';
            return;
        }

        // amplitud adaptativa según el ancho
        const baseX = window.innerWidth > 1200 ? 8 : 5;
        const baseY = window.innerWidth > 1200 ? 6 : 4;

        const onMove = (e)=>{
            const rect = hero.getBoundingClientRect();
            const x = (e.clientX - rect.left) / rect.width - 0.5; // -0.5 .. 0.5
            const y = (e.clientY - rect.top) / rect.height - 0.5;
            // mover ligeramente
            svgGroup.style.transform = `translate(${x*baseX}px, ${y*baseY}px)`;
        };

        hero.addEventListener('mousemove', onMove);

        // reset on leave
        hero.addEventListener('mouseleave', ()=>{
            svgGroup.style.transform = '';
        });

        // actualizar amplitud si se redimensiona
        window.addEventListener('resize', ()=>{
            // si se hace pequeño, quitamos parallax
            if(window.innerWidth < 900){
                svgGroup.style.transform = '';
            }
        });
    }

    // Pulse rings animation loop (scale + fade) using CSS variables for perf
    function initPulseRings(){
        const ring = document.querySelector('.pulse-rings .r2');
        if(!ring) return;
        let t = 0;
        function frame(){
            t += 1/60;
            const s = 1 + Math.sin(t*1.6)*0.08;
            ring.style.transform = `scale(${s})`;
            requestAnimationFrame(frame);
        }
        requestAnimationFrame(frame);
    }

    // Reveal sections with IntersectionObserver
    function initReveal(){
        const opts = { threshold: 0.12 };
        const obs = new IntersectionObserver((entries)=>{
            entries.forEach(ent=>{
                if(ent.isIntersecting){
                    ent.target.classList.add('reveal-visible');
                    obs.unobserve(ent.target);
                }
            });
        }, opts);

        document.querySelectorAll('.section').forEach(el=>{
            el.classList.add('reveal-hidden');
            obs.observe(el);
        });
    }

    // Init when DOM ready
    function initAll(){
        initParallax();
        initPulseRings();
        initReveal();
    }

    if(document.readyState === 'loading'){
        document.addEventListener('DOMContentLoaded', initAll);
    } else {
        initAll();
    }
})();
