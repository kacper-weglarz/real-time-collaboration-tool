const token = localStorage.getItem('token');

if (!token) {
    window.location.href = '/loginsignup.html';
} else {
    fetch('/api/user/profile', {
        headers: { 'Authorization': 'Bearer ' + token }
    })
        .then(r => r.json())
        .then(user => {
        });
}