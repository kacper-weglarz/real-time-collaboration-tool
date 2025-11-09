/**
 * ładuje wybrany dokument po id
 */
function loadChosenDocument(id) {
    const token = localStorage.getItem('token'); //pobiera token
    if (!token) {
        window.location.href = 'userprofile.html'; //jesli nie ma tokenu wraca na profil usera
    } else {
        fetch(`/api/document/${id}`, { //endpoint GET jednego dokumentu
            method: 'GET',
            headers: { 'Authorization': 'Bearer ' + token }
        })
            .then(response => response.json())
            .then(doc => {
                const title = document.querySelector('.title'); //bierze html title
                const content = document.querySelector('.content'); //bierze html content
                title.textContent = doc.title; //zabiera title z response i ustawia w html title
                content.textContent = doc.content; //zabiera content z response i ustawia w html content
                console.log(doc);
            })
            .catch(err => console.error('Fetch error:', err));
    }
}

/**
 * po zaladowaniu html pobiera parametry URL i do id bierze @PathVariable id dokumentu
 */
document.addEventListener('DOMContentLoaded', () => {
        const params = new URLSearchParams(window.location.search);
        const id = params.get('id');

        if (id) {
            loadChosenDocument(id) //wywoluje zaladowanie dokumentu przekazuje id z URL
        } else {
            window.location.href = "/userprofile.html" //jesli nie ma id wraca na userprofile
        }
})

/**
 * AUTOZAPIS
 */
const titleInput = document.querySelector('.title'); //bierze html title
const contentInput = document.querySelector('.content'); //bierze html content

let timeoutId; //id opoznienia
const debounceDelay = 1000; //1sek

/**
 * Funkcja autosave i opoznienia zapisania, zapis po 1 sekundzie od ostatniej zmiany
 */
function scheduleAutoSave() {
    clearTimeout(timeoutId); // anulujemy timer, jesli user pisze
    timeoutId = setTimeout(() => { // ustawiamy nowy timer
        updateDocument(); //zapisujemy
    }, debounceDelay);
}

// wywoluemy autosave
titleInput.addEventListener('input', scheduleAutoSave);
contentInput.addEventListener('input', scheduleAutoSave);

/**
 * Update dokumentu
 */
function updateDocument() {
    const token = localStorage.getItem('token'); //pobieramy token
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id'); //pobiera z URL id dokumentu

    if (!token || !id) return; //jesli nie ma tokenu ani id przerywa

    fetch(`/api/document/${id}`, { //endpoint PUT
        method: 'PUT',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ //przekazuje w request wypelniony tekst z title i content ze stalych const Input
            title: titleInput.textContent,
            content: contentInput.textContent,
        })
    })
    .then(res => {
            if (!res.ok) throw new Error('Save failed'); //jesli nie zapisalo error
            return res.json(); //zwraca response
    })
    .then(doc => {
        console.log(doc);
    })
    .catch(err => console.error('Auto-save error:', err));
}



