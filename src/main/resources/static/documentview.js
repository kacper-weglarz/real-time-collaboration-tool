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

//get bttn delete document
document.getElementById('delete-doc').addEventListener('click', (e) => {
    e.preventDefault();
    deleteDocument();
})

/**
 * usuwa dokument
 */
function deleteDocument() {
    const token = localStorage.getItem('token'); //pobiera token
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id'); //pobiera z URL id dokumentu

    if (!token || !id) {
        window.location.href = 'userprofile.html'; //jesli nie ma tokenu wraca na profil usera
    } else {
        fetch(`/api/document/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(res => {
            if (res.ok) {
               window.location.href = 'userprofile.html'; //po usunieciu jesli response.ok wraca na profil usera
            } else {
               throw new Error('Failed to delete document');
            }
        })
        .catch(error => {
            console.error('Error deleting document:', error);
        });
    }
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
        window.location.href = '/userprofile.html';
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
//wroc bttn
document.getElementById('go-back').addEventListener('click', (e) => {
    e.preventDefault();
    window.location.href = 'userprofile.html';
})
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