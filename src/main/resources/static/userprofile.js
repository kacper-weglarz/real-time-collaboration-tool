/**
 * Pobiera token sesji
 */
const token = localStorage.getItem('token');


/**
 * ładuje profil uzytkownika
 */
function loadUserProfile() {
    if (!token) {
        window.location.href = '/loginsignup.html'; // jesli nie ma tokenu wraca na strone logowania
    } else {
        fetch('/api/user/profile', { //endpoint do profilu usera
            headers: { 'Authorization': 'Bearer' + token } //sprawdza token
        })
            .then(r => r.json()) //zwraca odpwoiedz response
            .then(user => {
            });
    }
}

/**
 * wywouluje metoedy po zaloadowaniu strony
 */
document.addEventListener('DOMContentLoaded', () => {
    loadUserProfile();
    loadDocuments();
});

/**
 * laduje dokumentu usera
 */
function loadDocuments() {
    fetch('/api/document', { //endpoint do pobrania dokumentow usera
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + token }
    }).then(response => response.json())
    .then(documents => {
        const container = document.querySelector('.main-grid-layout'); //wybiera miejsce w html
        container.innerHTML = ''; //kasuje poprzednia zawartosc
        documents.forEach(document => { //dla kazdego response dokumentu
            const html =`<div class="single-document" data-id="${document.id}" onclick="openDocument(${document.id})"> 
                                    <h3>${document.title}</h3>  
                                    <p>${document.content}</p> 
                               </div>` //tworzy diva z onclick otwarciem i wypelnieniem tytulem oraz zawartoscia
            container.innerHTML += html;
        })
    })
}

/**
 * otwiera wybrany dokument i przekierowuje do documentview.html
 */
function openDocument(id) {
    window.location.href = `/documentview.html?id=${id}`
}

