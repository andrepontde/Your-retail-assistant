// Retail Management System API Client
class RetailAPI {
    constructor() {
        this.baseURL = 'http://localhost:8080/api';
        this.token = localStorage.getItem('jwt_token');
        this.updateAuthStatus();
    }

    // Helper method to make API requests
    async makeRequest(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            }
        };

        if (this.token) {
            defaultOptions.headers['Authorization'] = `Bearer ${this.token}`;
        }

        const finalOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers
            }
        };

        try {
            const response = await fetch(url, finalOptions);
            const responseText = await response.text();
            
            let data;
            try {
                data = responseText ? JSON.parse(responseText) : {};
            } catch (e) {
                data = responseText;
            }

            const result = {
                ok: response.ok,
                status: response.status,
                statusText: response.statusText,
                data: data
            };

            this.displayResponse(endpoint, finalOptions, result);
            return result;
        } catch (error) {
            const errorResult = {
                ok: false,
                status: 0,
                statusText: 'Network Error',
                data: error.message
            };
            this.displayResponse(endpoint, finalOptions, errorResult);
            return errorResult;
        }
    }

    // Display API response in the UI
    displayResponse(endpoint, options, result) {
        const output = document.getElementById('response-output');
        const timestamp = new Date().toLocaleTimeString();
        
        const logEntry = `
[${timestamp}] ${options.method || 'GET'} ${endpoint}
Status: ${result.status} ${result.statusText}
${result.ok ? '✅ SUCCESS' : '❌ ERROR'}

Response:
${JSON.stringify(result.data, null, 2)}

${'='.repeat(80)}
`;
        
        output.textContent = logEntry + output.textContent;
        
        // Show success/error message
        if (result.ok) {
            this.showMessage('Operation completed successfully!', 'success');
        } else {
            this.showMessage(`Error: ${result.status} - ${result.data.message || result.statusText}`, 'error');
        }
    }

    // Show temporary message
    showMessage(message, type) {
        const existing = document.querySelector('.temp-message');
        if (existing) existing.remove();

        const messageDiv = document.createElement('div');
        messageDiv.className = `temp-message ${type}-message`;
        messageDiv.textContent = message;
        
        document.querySelector('.container').insertBefore(messageDiv, document.querySelector('.section'));
        
        setTimeout(() => messageDiv.remove(), 5000);
    }

    // Update authentication status in UI
    updateAuthStatus() {
        const statusElement = document.getElementById('auth-status');
        const logoutBtn = document.getElementById('logout-btn');
        const authSection = document.getElementById('auth-section');
        const itemsSection = document.getElementById('items-section');
        const inventorySection = document.getElementById('inventory-section');

        if (this.token) {
            statusElement.textContent = 'Authenticated ✅';
            statusElement.className = 'authenticated';
            logoutBtn.style.display = 'inline-block';
            authSection.style.display = 'none';
            itemsSection.style.display = 'block';
            inventorySection.style.display = 'block';
        } else {
            statusElement.textContent = 'Not Authenticated';
            statusElement.className = '';
            logoutBtn.style.display = 'none';
            authSection.style.display = 'block';
            itemsSection.style.display = 'none';
            inventorySection.style.display = 'none';
        }
    }

    // Set authentication token
    setToken(token) {
        this.token = token;
        localStorage.setItem('jwt_token', token);
        this.updateAuthStatus();
    }

    // Clear authentication
    clearAuth() {
        this.token = null;
        localStorage.removeItem('jwt_token');
        this.updateAuthStatus();
    }

    // Authentication methods
    async login(username, password) {
        return await this.makeRequest('/users/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
    }

    async register(userData) {
        return await this.makeRequest('/users/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    // Item methods
    async createItem(itemData) {
        return await this.makeRequest('/items', {
            method: 'POST',
            body: JSON.stringify(itemData)
        });
    }

    async getAllItems() {
        return await this.makeRequest('/items');
    }

    async getItemById(id) {
        return await this.makeRequest(`/items/${id}`);
    }

    async getItemsByCategory(category) {
        return await this.makeRequest(`/items/category/${category}`);
    }

    async getItemsByName(name) {
        return await this.makeRequest(`/items/name/${name}`);
    }

    // Inventory methods
    async addStock(itemId, quantity) {
        return await this.makeRequest('/inventory/add-stock', {
            method: 'POST',
            body: JSON.stringify({ itemId, quantity })
        });
    }

    async removeStock(itemId, quantity) {
        return await this.makeRequest('/inventory/remove-stock', {
            method: 'POST',
            body: JSON.stringify({ itemId, quantity })
        });
    }

    async getStockLevel(itemId) {
        return await this.makeRequest(`/inventory/stock/${itemId}`);
    }

    async getMyStoreInventory() {
        return await this.makeRequest('/inventory/my-store');
    }

    async getLowStockItems() {
        return await this.makeRequest('/inventory/low-stock');
    }

    async reserveStock(itemId, quantity) {
        return await this.makeRequest('/inventory/reserve-stock', {
            method: 'POST',
            body: JSON.stringify({ itemId, quantity })
        });
    }

    async transferStock(itemId, quantity, toStoreId) {
        return await this.makeRequest('/inventory/transfer-stock', {
            method: 'POST',
            body: JSON.stringify({ itemId, quantity, toStoreId })
        });
    }
}

// Initialize API client
const api = new RetailAPI();

// Authentication functions
async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    
    if (!username || !password) {
        api.showMessage('Please enter both username and password', 'error');
        return;
    }

    const result = await api.login(username, password);
    if (result.ok && result.data.token) {
        api.setToken(result.data.token);
        api.showMessage(`Welcome ${result.data.user.fullName}!`, 'success');
    }
}

async function register() {
    const userData = {
        username: document.getElementById('reg-username').value,
        email: document.getElementById('reg-email').value,
        firstName: document.getElementById('reg-firstName').value,
        lastName: document.getElementById('reg-lastName').value,
        role: document.getElementById('reg-role').value,
        password: document.getElementById('reg-password').value,
        primaryStoreId: parseInt(document.getElementById('reg-storeId').value)
    };

    if (!userData.username || !userData.email || !userData.password) {
        api.showMessage('Please fill in all required fields', 'error');
        return;
    }

    await api.register(userData);
}

function logout() {
    api.clearAuth();
    api.showMessage('Logged out successfully', 'success');
}

// Item functions
async function createItem() {
    const itemData = {
        name: document.getElementById('item-name').value,
        description: document.getElementById('item-description').value,
        category: document.getElementById('item-category').value,
        price: parseFloat(document.getElementById('item-price').value),
        brand: document.getElementById('item-brand').value,
        size: document.getElementById('item-size').value,
        color: document.getElementById('item-color').value
    };

    const initialQuantity = document.getElementById('item-initialQuantity').value;
    if (initialQuantity) {
        itemData.initialQuantity = parseInt(initialQuantity);
    }

    if (!itemData.name || !itemData.category || !itemData.price) {
        api.showMessage('Please fill in required fields (name, category, price)', 'error');
        return;
    }

    await api.createItem(itemData);
}

async function getAllItems() {
    const result = await api.getAllItems();
    if (result.ok) {
        displayItems(result.data, 'items-list');
    }
}

async function getItemById() {
    const id = document.getElementById('get-item-id').value;
    if (!id) {
        api.showMessage('Please enter an item ID', 'error');
        return;
    }
    
    const result = await api.getItemById(id);
    if (result.ok) {
        displayItems([result.data], 'item-detail');
    }
}

async function getItemsByCategory() {
    const category = document.getElementById('get-category').value;
    if (!category) {
        api.showMessage('Please enter a category', 'error');
        return;
    }
    
    const result = await api.getItemsByCategory(category);
    if (result.ok) {
        displayItems(result.data, 'category-items');
    }
}

async function getItemsByName() {
    const name = document.getElementById('get-name').value;
    if (!name) {
        api.showMessage('Please enter an item name', 'error');
        return;
    }
    
    const result = await api.getItemsByName(name);
    if (result.ok) {
        displayItems(result.data, 'name-items');
    }
}

// Inventory functions
async function addStock() {
    const itemId = parseInt(document.getElementById('add-item-id').value);
    const quantity = parseInt(document.getElementById('add-quantity').value);
    
    if (!itemId || !quantity) {
        api.showMessage('Please enter both item ID and quantity', 'error');
        return;
    }
    
    await api.addStock(itemId, quantity);
}

async function removeStock() {
    const itemId = parseInt(document.getElementById('remove-item-id').value);
    const quantity = parseInt(document.getElementById('remove-quantity').value);
    
    if (!itemId || !quantity) {
        api.showMessage('Please enter both item ID and quantity', 'error');
        return;
    }
    
    await api.removeStock(itemId, quantity);
}

async function checkStock() {
    const itemId = document.getElementById('check-item-id').value;
    if (!itemId) {
        api.showMessage('Please enter an item ID', 'error');
        return;
    }
    
    const result = await api.getStockLevel(itemId);
    if (result.ok) {
        displayInventory([result.data], 'stock-level');
    }
}

async function getMyStoreInventory() {
    const result = await api.getMyStoreInventory();
    if (result.ok) {
        displayInventory(result.data, 'my-inventory');
    }
}

async function getLowStockItems() {
    const result = await api.getLowStockItems();
    if (result.ok) {
        displayInventory(result.data, 'low-stock');
    }
}

async function reserveStock() {
    const itemId = parseInt(document.getElementById('reserve-item-id').value);
    const quantity = parseInt(document.getElementById('reserve-quantity').value);
    
    if (!itemId || !quantity) {
        api.showMessage('Please enter both item ID and quantity', 'error');
        return;
    }
    
    await api.reserveStock(itemId, quantity);
}

async function transferStock() {
    const itemId = parseInt(document.getElementById('transfer-item-id').value);
    const quantity = parseInt(document.getElementById('transfer-quantity').value);
    const toStoreId = parseInt(document.getElementById('transfer-to-store').value);
    
    if (!itemId || !quantity || !toStoreId) {
        api.showMessage('Please enter item ID, quantity, and destination store ID', 'error');
        return;
    }
    
    await api.transferStock(itemId, quantity, toStoreId);
}

// Display functions
function displayItems(items, containerId) {
    const container = document.getElementById(containerId);
    
    if (!items || items.length === 0) {
        container.innerHTML = '<p>No items found.</p>';
        return;
    }
    
    container.innerHTML = items.map(item => `
        <div class="item-card">
            <div class="item-header">
                <span class="item-name">${item.name}</span>
                <span class="item-price">$${item.price ? item.price.toFixed(2) : 'N/A'}</span>
            </div>
            <div class="item-details">
                <div class="detail-item">
                    <span class="detail-label">ID:</span>
                    <span class="detail-value">${item.id}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Category:</span>
                    <span class="detail-value">${item.category}</span>
                </div>
                ${item.brand ? `
                <div class="detail-item">
                    <span class="detail-label">Brand:</span>
                    <span class="detail-value">${item.brand}</span>
                </div>` : ''}
                ${item.size ? `
                <div class="detail-item">
                    <span class="detail-label">Size:</span>
                    <span class="detail-value">${item.size}</span>
                </div>` : ''}
                ${item.color ? `
                <div class="detail-item">
                    <span class="detail-label">Color:</span>
                    <span class="detail-value">${item.color}</span>
                </div>` : ''}
            </div>
            ${item.description ? `<p style="margin-top: 10px; color: #718096;">${item.description}</p>` : ''}
        </div>
    `).join('');
}

function displayInventory(inventoryItems, containerId) {
    const container = document.getElementById(containerId);
    
    if (!inventoryItems || inventoryItems.length === 0) {
        container.innerHTML = '<p>No inventory found.</p>';
        return;
    }
    
    container.innerHTML = inventoryItems.map(inv => {
        const stockLevel = inv.quantity > 10 ? 'high' : inv.quantity > 5 ? 'medium' : 'low';
        return `
            <div class="inventory-card">
                <div class="item-header">
                    <span class="item-name">${inv.itemName || `Item ID: ${inv.itemId}`}</span>
                    <span class="stock-level ${stockLevel}">${inv.quantity} in stock</span>
                </div>
                <div class="stock-info">
                    <div>
                        <strong>Available:</strong> ${inv.quantity}<br>
                        <strong>Reserved:</strong> ${inv.reservedQuantity || 0}
                    </div>
                    <div>
                        <strong>Min Level:</strong> ${inv.minStockLevel || 'Not set'}<br>
                        <strong>Max Level:</strong> ${inv.maxStockLevel || 'Not set'}
                    </div>
                </div>
                ${inv.storeName ? `<p><strong>Store:</strong> ${inv.storeName}</p>` : ''}
            </div>
        `;
    }).join('');
}

// Event listeners
document.getElementById('logout-btn').addEventListener('click', logout);

// Allow Enter key to trigger login
document.getElementById('login-password').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        login();
    }
});

// Clear form function
function clearForm(formId) {
    const form = document.getElementById(formId);
    const inputs = form.querySelectorAll('input');
    inputs.forEach(input => {
        if (input.type !== 'number' || !input.value) {
            input.value = '';
        }
    });
}

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    // Clear the response output on page load
    document.getElementById('response-output').textContent = 'Welcome to Retail Management System API Tester!\nPlease login to start testing the APIs.\n' + '='.repeat(80) + '\n';
});
