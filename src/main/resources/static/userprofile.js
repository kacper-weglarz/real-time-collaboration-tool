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
            headers: { 'Authorization': 'Bearer ' + token } //sprawdza token
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
        initSearchFilter(); //po zaladowaniu uruchom szukanie
    })
}

/**
 * szuka dokumentu wpisanego w polu do wyszukiwania
 */
function initSearchFilter() {
    const searchInput = document.querySelector('.header input[type="search"]'); //pobiera html input search
    const container = document.querySelector('.main-grid-layout'); //pobiera liste dokumentow

    if (!searchInput || !container) return;

    searchInput.addEventListener('input', function() { //jesli user zmienia cos to dziala
        const query = this.value.toLowerCase().trim(); //usun biale znaki i lowerCase
        const docs = container.querySelectorAll('.single-document'); //pobiera kazdy dokument z sigle-doc

        //sprawdza kazdy dokument i porownuje go z wpsisanym tekstem
        docs.forEach(doc => {
            const title = doc.querySelector('h3').textContent.toLowerCase();
            doc.style.display = title.includes(query) ? 'flex' : 'none'; // jesli jest to pokaz jesli nie ukryj none
        });
    });
}


/**
 * otwiera wybrany dokument i przekierowuje do documentview.html
 */
function openDocument(id) {
    window.location.href = `/documentview.html?id=${id}`
}

//get bttn creatae document
document.getElementById('create-doc').addEventListener('click', (e) => {
    e.preventDefault();
    createNewDocument();
})

/**
 * tworzy nowy dokument
 */
function createNewDocument() {


        const token = localStorage.getItem('token'); //pobiera token

        if (!token) {
            window.location.href = '/userprofile.html'
        } else {
            fetch('/api/document', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: "Document without title",
                    content: "",
                })
            })
                .then(r => {
                    if (!r.ok)
                        throw new Error("Failed to create a document");
                    return r.json();
                })
                .then(newDoc => {
                    window.location.href = `/documentview.html?id=${newDoc.id}`; //jesli nowy dokument utowrzony otwiera go z nowym id
                })
                .then(err => {
                    console.error("Error: " + err);
                })
        }
}
//logout bttn
document.getElementById("logout").addEventListener('click', (e) => {
    e.preventDefault();
    logout();
})

/**
 * wyloguj
 */
function logout() {
    localStorage.removeItem('token'); //usuwa token
    window.location.href = '/loginsignup.html'; //przenosi na strone loginsignup.html
}
