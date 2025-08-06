// Retail Management System API Client
class RetailAPI {
    constructor() {
        // Dynamically determine the base URL based on current page location
        const protocol = window.location.protocol;
        const hostname = window.location.hostname;
        const port = window.location.port || (protocol === 'https:' ? '443' : '80');
        
        // If accessing via localhost/127.0.0.1, use localhost, otherwise use the current hostname
        if (hostname === 'localhost' || hostname === '127.0.0.1') {
            this.baseURL = `${protocol}//${hostname}:8080/api`;
        } else {
            // When accessing from phone, use the same hostname/IP as the web page
            this.baseURL = `${protocol}//${hostname}:8080/api`;
        }
        
        console.log('API Base URL:', this.baseURL);
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
        const salesSection = document.getElementById('sales-section');

        if (this.token) {
            statusElement.textContent = 'Authenticated ✅';
            statusElement.className = 'authenticated';
            logoutBtn.style.display = 'inline-block';
            authSection.style.display = 'none';
            itemsSection.style.display = 'block';
            inventorySection.style.display = 'block';
            salesSection.style.display = 'block';
        } else {
            statusElement.textContent = 'Not Authenticated';
            statusElement.className = '';
            logoutBtn.style.display = 'none';
            authSection.style.display = 'block';
            itemsSection.style.display = 'none';
            inventorySection.style.display = 'none';
            salesSection.style.display = 'none';
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

    // SKU-based item lookups
    async getItemBySKU(sku) {
        return await this.makeRequest(`/items/by-sku/${sku}`);
    }

    async getItemByUPC(upc) {
        return await this.makeRequest(`/items/by-upc/${upc}`);
    }

    async generateSKU(category, brand, variant) {
        return await this.makeRequest('/items/generate-sku', {
            method: 'POST',
            body: JSON.stringify({ category, brand, variant })
        });
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

    // SKU-based inventory methods
    async addStockBySKU(sku, quantity) {
        return await this.makeRequest('/inventory/add-stock-by-sku', {
            method: 'POST',
            body: JSON.stringify({ sku, quantity })
        });
    }

    async removeStockBySKU(sku, quantity) {
        return await this.makeRequest('/inventory/remove-stock-by-sku', {
            method: 'POST',
            body: JSON.stringify({ sku, quantity })
        });
    }

    async getStockBySKU(sku) {
        return await this.makeRequest(`/inventory/stock-by-sku/${sku}`);
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
        variant: document.getElementById('item-variant').value,
        sku: document.getElementById('item-sku').value,
        upc: document.getElementById('item-upc').value
    };

    const initialQuantity = document.getElementById('item-initialQuantity').value;
    if (initialQuantity) {
        itemData.initialQuantity = parseInt(initialQuantity);
    }

    if (!itemData.name || !itemData.category || !itemData.price) {
        api.showMessage('Please fill in required fields (name, category, price)', 'error');
        return;
    }

    // Auto-generate SKU if not provided
    if (!itemData.sku && itemData.category && itemData.brand) {
        const skuResult = await generateSKUForItem();
        if (skuResult) {
            itemData.sku = document.getElementById('item-sku').value;
            itemData.upc = document.getElementById('item-upc').value;
        }
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

async function getItemBySKU() {
    const sku = document.getElementById('get-sku').value;
    if (!sku) {
        api.showMessage('Please enter a SKU', 'error');
        return;
    }
    
    const result = await api.getItemBySKU(sku);
    if (result.ok) {
        displayItems([result.data], 'sku-items');
    }
}

async function getItemByUPC() {
    const upc = document.getElementById('get-upc').value;
    if (!upc) {
        api.showMessage('Please enter a UPC', 'error');
        return;
    }
    
    const result = await api.getItemByUPC(upc);
    if (result.ok) {
        displayItems([result.data], 'upc-items');
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

// SKU-based inventory functions
async function addStockBySKU() {
    const sku = document.getElementById('add-sku').value;
    const quantity = parseInt(document.getElementById('add-sku-quantity').value);
    
    if (!sku || !quantity) {
        api.showMessage('Please enter both SKU and quantity', 'error');
        return;
    }
    
    await api.addStockBySKU(sku, quantity);
}

async function removeStockBySKU() {
    const sku = document.getElementById('remove-sku').value;
    const quantity = parseInt(document.getElementById('remove-sku-quantity').value);
    
    if (!sku || !quantity) {
        api.showMessage('Please enter both SKU and quantity', 'error');
        return;
    }
    
    await api.removeStockBySKU(sku, quantity);
}

async function checkStockBySKU() {
    const sku = document.getElementById('check-sku').value;
    if (!sku) {
        api.showMessage('Please enter a SKU', 'error');
        return;
    }
    
    const result = await api.getStockBySKU(sku);
    if (result.ok) {
        displayInventory([result.data], 'sku-stock-level');
    }
}

// Display functions
function displayItems(items, containerId) {
    const container = document.getElementById(containerId);
    
    // Ensure items is an array
    if (!items) {
        container.innerHTML = '<p>No items found.</p>';
        return;
    }
    
    // Convert single item to array
    const itemsArray = Array.isArray(items) ? items : [items];
    
    if (itemsArray.length === 0) {
        container.innerHTML = '<p>No items found.</p>';
        return;
    }
    
    container.innerHTML = itemsArray.map(item => `
        <div class="item-card">
            <div class="item-header">
                <span class="item-name">${item.name}</span>
                <span class="item-price">$${item.price ? item.price.toFixed(2) : 'N/A'}</span>
            </div>
            <div class="item-details">
                ${item.sku ? `
                <div class="detail-item">
                    <span class="detail-label">SKU:</span>
                    <span class="detail-value" style="font-weight: bold; color: #2563eb;">${item.sku}</span>
                </div>` : ''}
                ${item.upc ? `
                <div class="detail-item">
                    <span class="detail-label">UPC:</span>
                    <span class="detail-value">${item.upc}</span>
                </div>` : ''}
                <div class="detail-item">
                    <span class="detail-label">Category:</span>
                    <span class="detail-value">${item.category}</span>
                </div>
                ${item.brand ? `
                <div class="detail-item">
                    <span class="detail-label">Brand:</span>
                    <span class="detail-value">${item.brand}</span>
                </div>` : ''}
                ${item.variant ? `
                <div class="detail-item">
                    <span class="detail-label">Variant:</span>
                    <span class="detail-value">${item.variant}</span>
                </div>` : ''}
                <div class="detail-item" style="font-size: 0.85em; color: #6b7280;">
                    <span class="detail-label">DB ID:</span>
                    <span class="detail-value">${item.id}</span>
                </div>
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
                ${inv.itemSku ? `
                <div class="sku-info">
                    <strong>SKU:</strong> <span style="font-family: monospace; color: #2563eb; font-weight: bold;">${inv.itemSku}</span>
                    ${inv.itemUpc ? `<br><strong>UPC:</strong> <span style="font-family: monospace;">${inv.itemUpc}</span>` : ''}
                </div>` : ''}
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
                ${inv.itemId ? `<p style="font-size: 0.85em; color: #6b7280;"><strong>DB ID:</strong> ${inv.itemId}</p>` : ''}
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

// Display error message function
function displayError(message) {
    api.showMessage(`Error: ${message}`, 'error');
    
    // Also log to console for debugging
    console.error('Error:', message);
    
    // Update the response output with error
    const output = document.getElementById('response-output');
    const timestamp = new Date().toLocaleTimeString();
    const logEntry = `
[${timestamp}] ERROR
❌ ${message}

${'='.repeat(80)}
`;
    output.textContent = logEntry + output.textContent;
}

// Display response function
function displayResponse(response) {
    // This should only be called for successful responses
    if (response) {
        const output = document.getElementById('response-output');
        const timestamp = new Date().toLocaleTimeString();
        const logEntry = `
[${timestamp}] API Response
✅ SUCCESS

Response:
${JSON.stringify(response, null, 2)}

${'='.repeat(80)}
`;
        output.textContent = logEntry + output.textContent;
        api.showMessage('Operation completed successfully!', 'success');
    }
}

// ========================= SALES FUNCTIONS =========================

// Add a new sale item row
function addSaleItemRow() {
    const container = document.getElementById('sale-items-container');
    const newRow = document.createElement('div');
    newRow.className = 'sale-item-row';
    newRow.innerHTML = `
        <input type="number" class="item-id" placeholder="Item ID">
        <input type="number" class="item-quantity" placeholder="Quantity" min="1" value="1">
        <button type="button" class="btn btn-warning" onclick="removeSaleItemRow(this)">-</button>
    `;
    container.appendChild(newRow);
}

// Remove a sale item row
function removeSaleItemRow(button) {
    const container = document.getElementById('sale-items-container');
    if (container.children.length > 1) {
        button.parentElement.remove();
    }
}

// Create a new sale
async function createSale() {
    const customerEmail = document.getElementById('customer-email').value.trim();
    const customerPhone = document.getElementById('customer-phone').value.trim();
    const paymentMethod = document.getElementById('payment-method').value;
    
    // Collect sale items
    const itemRows = document.querySelectorAll('.sale-item-row');
    const saleItems = [];
    
    for (const row of itemRows) {
        const itemId = row.querySelector('.item-id').value;
        const quantity = row.querySelector('.item-quantity').value;
        
        if (itemId && quantity && quantity > 0) {
            saleItems.push({
                itemId: parseInt(itemId),
                quantity: parseInt(quantity)
            });
        }
    }
    
    if (saleItems.length === 0) {
        displayError('Please add at least one item to the sale');
        return;
    }
    
    const saleData = {
        customerEmail: customerEmail || null,
        customerPhone: customerPhone || null,
        paymentMethod: paymentMethod,
        saleItems: saleItems.map(item => ({
            item: { id: item.itemId }, // Backend expects { item: { id: ... } }
            quantity: item.quantity
        }))
    };
    
    try {
        const result = await api.makeRequest('/sales', {
            method: 'POST',
            body: JSON.stringify(saleData)
        });
        
        if (result.ok && result.data) {
            const response = result.data;
            api.displayResponse('/sales', { method: 'POST', body: JSON.stringify(saleData) }, result);
            
            document.getElementById('sale-result').innerHTML = `
                <div class="success">
                    <h4>Sale Created Successfully!</h4>
                    <p><strong>Sale ID:</strong> ${response.id}</p>
                    <p><strong>Total Amount:</strong> $${response.totalAmount}</p>
                    <p><strong>Tax Amount:</strong> $${response.taxAmount}</p>
                    <p><strong>Grand Total:</strong> $${response.grandTotal}</p>
                    <button class="btn btn-secondary" onclick="generateReceiptForSale(${response.id})">Generate Receipt</button>
                </div>
            `;
            clearForm('sales-section');
        } else {
            displayError(result.data?.message || 'Failed to create sale');
        }
    } catch (error) {
        console.error('Error creating sale:', error);
        displayError('Error creating sale: ' + (error.message || 'Unknown error'));
    }
}

// Generate receipt for a specific sale ID
async function generateReceiptForSale(saleId) {
    document.getElementById('receipt-sale-id').value = saleId;
    await generateReceipt();
}

// Generate receipt
async function generateReceipt() {
    const saleId = document.getElementById('receipt-sale-id').value;
    
    if (!saleId) {
        displayError('Please enter a Sale ID');
        return;
    }
    
    try {
        const result = await api.makeRequest(`/sales/${saleId}/receipt`, {
            method: 'GET'
        });
        
        if (result.ok && result.data) {
            const response = result.data;
            api.displayResponse(`/sales/${saleId}/receipt`, { method: 'GET' }, result);
            
            const receiptHtml = formatReceipt(response);
            document.getElementById('receipt-display').innerHTML = receiptHtml;
        } else {
            displayError(result.data?.message || 'Failed to generate receipt');
        }
    } catch (error) {
        console.error('Error generating receipt:', error);
        displayError('Error generating receipt: ' + (error.message || 'Unknown error'));
    }
}

// Format receipt for display
function formatReceipt(receiptData) {
    const receipt = receiptData;
    
    let html = `
        <div class="receipt">
            <div class="receipt-header">
                <h3>🧾 Sales Receipt</h3>
                <p><strong>Receipt #:</strong> ${receipt.receiptNumber}</p>
                <p><strong>Date:</strong> ${new Date(receipt.saleDate).toLocaleString()}</p>
                <p><strong>Store:</strong> ${receipt.storeName}</p>
                <p><strong>Address:</strong> ${receipt.storeAddress}</p>
            </div>
            
            <div class="receipt-customer">
                ${receipt.customerName ? `<p><strong>Customer:</strong> ${receipt.customerName}</p>` : ''}
                ${receipt.customerEmail ? `<p><strong>Email:</strong> ${receipt.customerEmail}</p>` : ''}
                <p><strong>Payment:</strong> ${receipt.paymentMethod}</p>
            </div>
            
            <div class="receipt-items">
                <h4>Items:</h4>
                <table class="receipt-table">
                    <thead>
                        <tr>
                            <th>Item</th>
                            <th>Qty</th>
                            <th>Price</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
    `;
    
    receipt.items.forEach(item => {
        html += `
            <tr>
                <td>${item.itemName}</td>
                <td>${item.quantity}</td>
                <td>$${item.unitPrice.toFixed(2)}</td>
                <td>$${item.lineTotal.toFixed(2)}</td>
            </tr>
        `;
    });
    
    html += `
                    </tbody>
                </table>
            </div>
            
            <div class="receipt-totals">
                <p><strong>Subtotal:</strong> $${receipt.subtotal.toFixed(2)}</p>
                <p><strong>Tax:</strong> $${receipt.tax.toFixed(2)}</p>
                <p class="grand-total"><strong>Grand Total: $${receipt.total.toFixed(2)}</strong></p>
            </div>
            
            <div class="receipt-footer">
                <p>Thank you for your business!</p>
                <button class="btn btn-secondary" onclick="printReceipt()">🖨️ Print Receipt</button>
            </div>
        </div>
    `;
    
    return html;
}

// Print receipt
function printReceipt() {
    const receiptContent = document.querySelector('.receipt').outerHTML;
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>Receipt</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                .receipt { max-width: 400px; margin: 0 auto; }
                .receipt-header, .receipt-customer, .receipt-footer { text-align: center; margin-bottom: 15px; }
                .receipt-table { width: 100%; border-collapse: collapse; margin: 10px 0; }
                .receipt-table th, .receipt-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                .receipt-table th { background-color: #f5f5f5; }
                .receipt-totals { text-align: right; margin-top: 15px; }
                .grand-total { font-size: 1.2em; border-top: 2px solid #000; padding-top: 5px; }
                @media print { button { display: none; } }
            </style>
        </head>
        <body>
            ${receiptContent}
        </body>
        </html>
    `);
    printWindow.document.close();
    printWindow.print();
}

// Get all sales for the current store
async function getAllSales() {
    try {
        const result = await api.makeRequest('/sales', {
            method: 'GET'
        });
        
        if (result.ok && result.data) {
            const response = result.data;
            api.displayResponse('/sales', { method: 'GET' }, result);
            
            if (response && Array.isArray(response)) {
                let html = '<div class="sales-list">';
                
                if (response.length === 0) {
                    html += '<p>No sales found for your store.</p>';
                } else {
                    html += '<table class="sales-table"><thead><tr><th>ID</th><th>Date</th><th>Customer</th><th>Payment</th><th>Total</th><th>Actions</th></tr></thead><tbody>';
                    
                    response.forEach(sale => {
                        html += `
                            <tr>
                                <td>${sale.id}</td>
                                <td>${new Date(sale.saleDate).toLocaleDateString()}</td>
                                <td>${sale.customerName || 'Walk-in'}</td>
                                <td>${sale.paymentMethod}</td>
                                <td>$${sale.grandTotal.toFixed(2)}</td>
                                <td>
                                    <button class="btn btn-secondary" onclick="generateReceiptForSale(${sale.id})">Receipt</button>
                                </td>
                            </tr>
                        `;
                    });
                    
                    html += '</tbody></table>';
                }
                
                html += '</div>';
                document.getElementById('all-sales').innerHTML = html;
            }
        } else {
            displayError(result.data?.message || 'Failed to retrieve sales');
        }
    } catch (error) {
        console.error('Error getting sales:', error);
        displayError('Error getting sales: ' + (error.message || 'Unknown error'));
    }
}

// ========================================
// SKU AND BARCODE FUNCTIONS
// ========================================

/**
 * Generate SKU for the current item form
 */
async function generateSKUForItem() {
    const category = document.getElementById('item-category').value;
    const brand = document.getElementById('item-brand').value;
    const variant = document.getElementById('item-variant').value;
    
    if (!category || !brand) {
        api.showMessage('Category and Brand are required for SKU generation', 'error');
        return false;
    }
    
    try {
        const result = await api.makeRequest('/items/generate-sku', {
            method: 'POST',
            body: JSON.stringify({ 
                category: category, 
                brand: brand, 
                variant: variant 
            })
        });
        
        if (result.ok) {
            document.getElementById('item-sku').value = result.data.sku;
            document.getElementById('item-upc').value = result.data.upc;
            updateSKUPreview();
            api.showMessage('SKU generated successfully: ' + result.data.sku, 'success');
            return true;
        } else {
            api.showMessage('Failed to generate SKU: ' + (result.data?.error || 'Unknown error'), 'error');
            return false;
        }
    } catch (error) {
        console.error('Error generating SKU:', error);
        api.showMessage('Error generating SKU: ' + error.message, 'error');
        return false;
    }
}

/**
 * Update SKU preview as user types
 */
function updateSKUPreview() {
    const category = document.getElementById('item-category').value;
    const brand = document.getElementById('item-brand').value;
    const variant = document.getElementById('item-variant').value;
    
    const previewElement = document.getElementById('sku-preview');
    
    if (category && brand) {
        const categoryCode = getCategoryCode(category);
        const brandCode = getBrandCode(brand);
        const variantCode = variant ? cleanForSKU(variant) : '';
        
        let preview = `${categoryCode}-${brandCode}`;
        if (variantCode) {
            preview += `-${variantCode}`;
        }
        preview += '-XXX';
        
        previewElement.textContent = `Preview: ${preview}`;
        previewElement.style.color = '#28a745';
    } else {
        previewElement.textContent = 'Enter category and brand to see SKU preview';
        previewElement.style.color = '#666';
    }
}

/**
 * Get item by SKU
 */
async function getItemBySKU() {
    const sku = document.getElementById('get-sku').value;
    
    if (!sku) {
        api.showMessage('Please enter a SKU', 'error');
        return;
    }
    
    try {
        const result = await api.makeRequest(`/items/by-sku/${encodeURIComponent(sku)}`, {
            method: 'GET'
        });
        
        if (result.ok) {
            document.getElementById('sku-item').innerHTML = formatItemDisplay(result.data);
            displayResponse(result.data);
        } else {
            document.getElementById('sku-item').innerHTML = '<p style="color: #dc3545;">Item not found</p>';
            api.showMessage('Item not found with SKU: ' + sku, 'error');
        }
    } catch (error) {
        console.error('Error getting item by SKU:', error);
        api.showMessage('Error getting item: ' + error.message, 'error');
    }
}

/**
 * Get item by UPC
 */
async function getItemByUPC() {
    const upc = document.getElementById('get-upc').value;
    
    if (!upc) {
        api.showMessage('Please enter a UPC', 'error');
        return;
    }
    
    try {
        const result = await api.makeRequest(`/items/by-upc/${encodeURIComponent(upc)}`, {
            method: 'GET'
        });
        
        if (result.ok) {
            document.getElementById('upc-item').innerHTML = formatItemDisplay(result.data);
            displayResponse(result.data);
        } else {
            document.getElementById('upc-item').innerHTML = '<p style="color: #dc3545;">Item not found</p>';
            api.showMessage('Item not found with UPC: ' + upc, 'error');
        }
    } catch (error) {
        console.error('Error getting item by UPC:', error);
        api.showMessage('Error getting item: ' + error.message, 'error');
    }
}

/**
 * Helper function to format item display
 */
function formatItemDisplay(item) {
    return `
        <div class="item-details">
            <h4>${item.name} (ID: ${item.id})</h4>
            <p><strong>SKU:</strong> ${item.sku || 'Not set'}</p>
            <p><strong>UPC:</strong> ${item.upc || 'Not set'}</p>
            <p><strong>Category:</strong> ${item.category}</p>
            <p><strong>Brand:</strong> ${item.brand || 'Not set'}</p>
            <p><strong>Variant:</strong> ${item.variant || 'Not set'}</p>
            <p><strong>Price:</strong> $${item.price.toFixed(2)}</p>
            <p><strong>Description:</strong> ${item.description || 'No description'}</p>
        </div>
    `;
}

// Helper functions for SKU generation
function getCategoryCode(category) {
    if (!category) return 'GEN';
    const upper = category.toUpperCase();
    if (upper.includes('ELECTRONIC') || upper.includes('TECH')) return 'ELE';
    if (upper.includes('CLOTH') || upper.includes('APPAREL')) return 'CLO';
    if (upper.includes('FOOD') || upper.includes('BEVERAGE')) return 'FOO';
    if (upper.includes('BOOK') || upper.includes('MEDIA')) return 'BOO';
    if (upper.includes('HOME') || upper.includes('FURNITURE')) return 'HOM';
    if (upper.includes('SPORT') || upper.includes('FITNESS')) return 'SPO';
    if (upper.includes('BEAUTY') || upper.includes('COSMETIC')) return 'BEA';
    if (upper.includes('AUTO') || upper.includes('CAR')) return 'AUT';
    if (upper.includes('TOY') || upper.includes('GAME')) return 'TOY';
    if (upper.includes('TOOL') || upper.includes('HARDWARE')) return 'TOL';
    return 'GEN';
}

function getBrandCode(brand) {
    if (!brand) return 'GEN';
    const upper = brand.toUpperCase();
    if (upper.includes('SAMSUNG')) return 'SAM';
    if (upper.includes('APPLE')) return 'APP';
    if (upper.includes('NIKE')) return 'NIK';
    if (upper.includes('ADIDAS')) return 'ADI';
    if (upper.includes('SONY')) return 'SON';
    if (upper.includes('MICROSOFT')) return 'MIC';
    if (upper.includes('GOOGLE')) return 'GOO';
    if (upper.includes('COCA')) return 'COK';
    if (upper.includes('PEPSI')) return 'PEP';
    return cleanForSKU(brand);
}

function cleanForSKU(input) {
    if (!input) return 'GEN';
    const cleaned = input.replace(/[^A-Za-z0-9]/g, '').toUpperCase();
    return cleaned.substring(0, 6) || 'GEN';
}

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    // Clear the response output on page load
    document.getElementById('response-output').textContent = 'Welcome to Retail Management System API Tester!\nPlease login to start testing the APIs.\n' + '='.repeat(80) + '\n';
});
