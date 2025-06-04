const modal = document.getElementById('modal');
const learnMoreBtn = document.getElementById('learn-more');
const closeBtn = document.querySelector('.close');

learnMoreBtn.addEventListener('click', () => {
    modal.style.display = 'block';
    setTimeout(() => { modal.style.opacity = '1'; }, 10);
});

closeBtn.addEventListener('click', () => {
    modal.style.opacity = '0';
    setTimeout(() => { modal.style.display = 'none'; }, 300);
});

window.addEventListener('click', (e) => {
    if (e.target == modal) {
        modal.style.opacity = '0';
        setTimeout(() => { modal.style.display = 'none'; }, 300);
    }
});