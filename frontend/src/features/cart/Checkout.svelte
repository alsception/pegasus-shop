<script lang="ts">
  import axios from "axios";

 const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  let name = "";
  let email = "";
  let address = "";
  let paymentMethod = "";


  async function submitForm() {
    const url = API_BASE_URL + '/cart/checkout';
    const payload = {
      email,
      name,
      address,
      paymentMethod
    };

    try {
      const response = await axios.post(url, payload, {
        headers: {
          Authorization: 'Bearer ' + localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      });
      console.log('Success:', response.data);
    } catch (error) {
      console.error('Error submitting form:', error);
    }
  }
</script>

<div class="w-full max-w-xl bg-white rounded-lg shadow-md p-8">
  <h1 class="text-2xl font-bold mb-6">Checkout</h1>

<form class="space-y-5" on:submit|preventDefault={submitForm}>    
    <!-- Ime i prezime -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1" for="fullName"
        >Ime i prezime</label
      >
      <input
        id="fullName"
        type="text"
        class="w-full border border-gray-300 rounded px-4 py-2 focus:outline-none focus:ring focus:border-blue-400"
        placeholder="Petar Petrović"
        bind:value={name} required
      />
    </div>

    <!-- Email adresa -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1" for="email"
        >Email adresa <span class="text-red-500">*</span></label
      >
      <input
        id="email"
        type="email"
        class="w-full border border-gray-300 rounded px-4 py-2 focus:outline-none focus:ring focus:border-blue-400"
        placeholder="petar@example.com"
        bind:value={email} required
      />
    </div>

    <!-- Adresa za dostavu -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1" for="address"
        >Adresa za dostavu</label
      >
      <textarea
        id="address"
        class="w-full border border-gray-300 rounded px-4 py-2 focus:outline-none focus:ring focus:border-blue-400"
        rows="3"
        placeholder="Ulica i broj, grad, poštanski broj"
        bind:value={address}
      ></textarea>
    </div>

    <!-- Način plaćanja -->
     <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">
    Payment Method:
        <select bind:value={paymentMethod} required>
          <option value="" disabled selected>Select</option>
          <option value="credit_card" disabled>Credit Card</option>
          <option value="paypal" disabled>PayPal</option>
          <option value="bank_transfer">Bank Transfer / pouzećem</option>
        </select>
      </label>

    <div class="hidden">
      <label class="block text-sm font-medium text-gray-700 mb-2"
        >Način plaćanja</label
      >
      <div class="space-y-2">
        <label class="flex items-center space-x-2">
          <input
            type="radio"
            name="payment"
            value="card"
            class="text-blue-600 focus:ring-blue-500"
          />
          <span>Kreditna/debitna kartica</span>
        </label>
        <label class="flex items-center space-x-2">
          <input
            type="radio"
            name="payment"
            value="cash"
            class="text-blue-600 focus:ring-blue-500"
          />
          <span>Gotovina pouzećem</span>
        </label>
        <label class="flex items-center space-x-2">
          <input
            type="radio"
            name="payment"
            value="paypal"
            class="text-blue-600 focus:ring-blue-500"
          />
          <span>PayPal</span>
        </label>
      </div>
    </div>

    <br><br>
    <!-- Submit dugme -->
    <div>
      <button
        type="submit"
        class="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded"
      >
        Završi kupovinu
      </button>
    </div>
  </form>
</div>
