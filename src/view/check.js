document.addEventListener('DOMContentLoaded', function() {
    const navButtons = document.querySelectorAll('.nav-btn');

    navButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');

            // 1. Kiểm tra nếu là chuyển trang lớn (Login <-> Main)
            const targetPage = document.getElementById(targetId);
            if (targetPage && targetPage.classList.contains('content-page')) {
                document.querySelectorAll('.content-page').forEach(p => p.classList.remove('active'));
                targetPage.classList.add('active');
            }

            // 2. Kiểm tra nếu là chuyển sub-page trong Dashboard
            const targetSub = document.getElementById(targetId);
            if (targetSub && targetSub.classList.contains('sub-page')) {
                document.querySelectorAll('.sub-page').forEach(p => p.classList.remove('active'));
                targetSub.classList.add('active');

                // Đổi trạng thái active trên menu sidebar
                document.querySelectorAll('.sidebar li').forEach(li => li.classList.remove('active-link'));
                if(this.tagName === 'LI') this.classList.add('active-link');
            }
        });
    });
});