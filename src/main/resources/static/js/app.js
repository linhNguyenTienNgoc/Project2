// Coffee Shop Management System - Main JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Search functionality
    initializeSearch();
    
    // Form validation
    initializeFormValidation();
    
    // Dynamic cart functionality
    initializeCart();
    
    // Charts initialization
    initializeCharts();
    
    // Table status updates
    initializeTableStatus();
});

// Search functionality
function initializeSearch() {
    const searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(input => {
        input.addEventListener('input', function() {
            const keyword = this.value.toLowerCase();
            const table = this.closest('.card').querySelector('table');
            if (table) {
                const rows = table.querySelectorAll('tbody tr');
                rows.forEach(row => {
                    const text = row.textContent.toLowerCase();
                    row.style.display = text.includes(keyword) ? '' : 'none';
                });
            }
        });
    });
}

// Form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

// Dynamic cart functionality
function initializeCart() {
    const addToCartButtons = document.querySelectorAll('.add-to-cart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const menuId = this.dataset.menuId;
            const menuName = this.dataset.menuName;
            const menuPrice = this.dataset.menuPrice;
            
            addToCart(menuId, menuName, menuPrice);
        });
    });
}

function addToCart(menuId, menuName, menuPrice) {
    let cart = JSON.parse(localStorage.getItem('cart') || '[]');
    
    const existingItem = cart.find(item => item.menuId === menuId);
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            menuId: menuId,
            menuName: menuName,
            menuPrice: parseFloat(menuPrice),
            quantity: 1
        });
    }
    
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartDisplay();
    showToast('Đã thêm vào giỏ hàng!', 'success');
}

function updateCartDisplay() {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const cartCount = document.getElementById('cart-count');
    const cartTotal = document.getElementById('cart-total');
    
    if (cartCount) {
        cartCount.textContent = cart.reduce((sum, item) => sum + item.quantity, 0);
    }
    
    if (cartTotal) {
        const total = cart.reduce((sum, item) => sum + (item.menuPrice * item.quantity), 0);
        cartTotal.textContent = formatCurrency(total);
    }
}

function clearCart() {
    localStorage.removeItem('cart');
    updateCartDisplay();
    showToast('Đã xóa giỏ hàng!', 'info');
}

// Table status updates
function initializeTableStatus() {
    const statusButtons = document.querySelectorAll('.update-status');
    statusButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tableId = this.dataset.tableId;
            const newStatus = this.dataset.status;
            
            updateTableStatus(tableId, newStatus);
        });
    });
}

function updateTableStatus(tableId, newStatus) {
    fetch(`/tables/${tableId}/status`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: newStatus })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('Cập nhật trạng thái bàn thành công!', 'success');
            setTimeout(() => location.reload(), 1000);
        } else {
            showToast('Có lỗi xảy ra!', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Có lỗi xảy ra!', 'error');
    });
}

// Charts initialization
function initializeCharts() {
    const revenueChart = document.getElementById('revenueChart');
    if (revenueChart) {
        const ctx = revenueChart.getContext('2d');
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
                datasets: [{
                    label: 'Doanh thu (VNĐ)',
                    data: [1200000, 1900000, 1500000, 2100000, 1800000, 2500000, 2200000],
                    borderColor: '#6f4e37',
                    backgroundColor: 'rgba(111, 78, 55, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'Biểu đồ doanh thu tuần'
                    }
                }
            }
        });
    }
    
    const topItemsChart = document.getElementById('topItemsChart');
    if (topItemsChart) {
        const ctx = topItemsChart.getContext('2d');
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Cà phê đen', 'Cà phê sữa', 'Trà sữa', 'Bánh ngọt', 'Nước ép'],
                datasets: [{
                    data: [30, 25, 20, 15, 10],
                    backgroundColor: [
                        '#6f4e37',
                        '#8b4513',
                        '#d2691e',
                        '#28a745',
                        '#17a2b8'
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                    },
                    title: {
                        display: true,
                        text: 'Top món bán chạy'
                    }
                }
            }
        });
    }
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('vi-VN');
}

function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        createToastContainer();
    }
    
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;
    
    document.getElementById('toast-container').appendChild(toast);
    
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
    
    toast.addEventListener('hidden.bs.toast', function() {
        toast.remove();
    });
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
}

// Confirm delete
function confirmDelete(message = 'Bạn có chắc chắn muốn xóa?') {
    return confirm(message);
}

// Loading state
function showLoading(element) {
    element.disabled = true;
    element.innerHTML = '<span class="loading"></span> Đang xử lý...';
}

function hideLoading(element, originalText) {
    element.disabled = false;
    element.innerHTML = originalText;
}

// Export functions
window.CoffeeShopApp = {
    addToCart,
    clearCart,
    updateCartDisplay,
    formatCurrency,
    formatDate,
    showToast,
    confirmDelete,
    showLoading,
    hideLoading
}; 