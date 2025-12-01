
/**
 * Takes session token
 */
const token = localStorage.getItem('token');

/**
 * Loads user profile
 */
function loadUserProfile() {
    if (!token) {
        window.location.href = '/loginsignup.html';
    } else {
        fetch('/api/user/profile', {
            headers: { 'Authorization': 'Bearer ' + token } //Checks token
        })
            .then(r => r.json())
            .then(user => {
            });
    }
}

/**
 * Loads user documents
 */
function loadDocuments() {
    fetch('/api/document', {
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + token }
    }).then(response => response.json())
        .then(documents => {
            const container = document.querySelector('.main-grid-layout');
            container.innerHTML = '';
            documents.forEach(document => {
                const html =`<div class="single-document" data-id="${document.id}" onclick="openDocument(${document.id})"> 
                                    <h3>${document.title}</h3>  
                                    <p>${document.content}</p> 
                               </div>`
                container.innerHTML += html;
            })
            initSearchFilter();
        })
}

/**
 * Looks for documents by title written in search console
 */
function initSearchFilter() {
    const searchInput = document.querySelector('.header input[type="search"]');
    const container = document.querySelector('.main-grid-layout');

    if (!searchInput || !container) return;

    searchInput.addEventListener('input', function() {
        const query = this.value.toLowerCase().trim();
        const docs = container.querySelectorAll('.single-document');


        docs.forEach(doc => {
            const title = doc.querySelector('h3').textContent.toLowerCase();
            doc.style.display = title.includes(query) ? 'flex' : 'none';
        });
    });
}

/**
 * Opens chosen document
 */
function openDocument(id) {
    window.location.href = `/documentview.html?id=${id}`
}

/**
 * Creates new document
 */
function createNewDocument() {
    const token = localStorage.getItem('token');

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
                window.location.href = `/documentview.html?id=${newDoc.id}`;
            })
            .then(err => {
                console.error("Error: " + err);
            })
    }
}

/**
 * Logout
 */
function logout() {
    localStorage.removeItem('token');
    window.location.href = '/loginsignup.html';
}


/**
 * After HTML load method starts
 */
document.addEventListener('DOMContentLoaded', () => {
    loadUserProfile();
    loadDocuments();
});


document.getElementById('create-doc').addEventListener('click', (e) => {
    e.preventDefault();
    createNewDocument();
})


document.getElementById("logout").addEventListener('click', (e) => {
    e.preventDefault();
    logout();
})
