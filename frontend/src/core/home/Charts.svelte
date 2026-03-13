<script lang="ts">
  import { onMount } from "svelte";
  import { auth, getCurrentRole } from "../services/SessionStore";
  import type { Order } from "../../features/orders/Order";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
  const token = $auth.token;

  let orders: Order[] = [];
  let ordersNaCekanju: Order[] = [];
  let ordersUpripremi: Order[] = [];
  let ordersSpremni: Order[] = [];
  let ordersRestoran = 0;
  let ordersDostava = 0;
  let ordersTakeAway = 0;
  let totalAmount = 0;

  onMount(() => {
    processGoogleChart1();
    processGoogleChart2();
    loadOrders();
    loadTimesChart();
  });

  function processGoogleChart1() {
    google.charts.load("current", { packages: ["bar"] });
    google.charts.setOnLoadCallback(fetchAndDraw);

    async function fetchAndDraw() {
      try {
        // 1. Pozovi tvoj endpoint (prilagodi URL ako treba)
        const response0 = await fetch(
          API_BASE_URL + `/statistics/total-times`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        const response = await fetch(
          API_BASE_URL + `/statistics/total-orders`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        const backendData = await response.json();

        // Mapiramo niz objekata u format koji Google Charts razume
        const chartData = [
          ["Dan", "Broj narudžbina"], // Zaglavlje
        ];

        // Prolazimo kroz JSON i izvlačimo vrednosti u pod-nizove
        backendData.forEach(
          (item: { datum: string; broj_narudzbina: string }) => {
            chartData.push([item.datum, item.broj_narudzbina]);
          }
        );

        drawChart(chartData);
      } catch (error) {
        console.error("Greška pri učitavanju statistike:", error);
      }
    }

    function drawChart(formattedData: any[]) {
      var data = google.visualization.arrayToDataTable(formattedData);

      var options = {
        chart: {
          title: "Narudžbe po danima",
          subtitle: "Zadnjih 90 dana",
        },
        colors: ["#1b9e77"],
        height: 400,
        backgroundColor: "transparent",
        chartArea: {
          fill: "transparent",
          backgroundColor: "transparent",
        },
        // Podešavanje linija na vertikalnoj osi
        vAxis: {
          gridlines: {
            color: "oklch(0.67 0 0 / 0.41)", // Boja horizontalnih linija
            count: 4, // Opciono: broj linija
          },
          baselineColor: "#888", // Boja nulte (donje) linije
        },

        // Uklanjanje vertikalnih linija (obično lepše izgleda)
        hAxis: {
          gridlines: { color: "transparent" },
        },

        legend: { position: "none" },
      };

      var chart = new google.charts.Bar(document.getElementById("chart_div1"));
      chart.draw(data, google.charts.Bar.convertOptions(options));
    }
  }

  function processGoogleChart2() {
    google.charts.load("current", { packages: ["line"] });
    google.charts.setOnLoadCallback(fetchAndDraw);

    async function fetchAndDraw() {
      try {
        // 1. Pozovi tvoj endpoint (prilagodi URL ako treba)
        const response = await fetch(
          API_BASE_URL + `/statistics/total-amount`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        const backendData = await response.json();

        // Mapiramo niz objekata u format koji Google Charts razume
        const chartData = [
          ["Dan", "Promet €"], // Zaglavlje
        ];

        // Prolazimo kroz JSON i izvlačimo vrednosti u pod-nizove
        backendData.forEach(
          (item: {
            ukupni_promet: string; datum: string; broj_narudzbina: string 
            }) => {
            chartData.push([item.datum, item.ukupni_promet]);
          }
        );

        drawChart(chartData);
      } catch (error) {
        console.error("Greška pri učitavanju statistike:", error);
      }
    }

    function drawChart(formattedData: any[]) {
      var data = google.visualization.arrayToDataTable(formattedData);

      var options = {
        chart: {
          title: "Promet po danima",
          subtitle: "Zadnjih 90 dana",
        },
        colors: ["#3E63DD"],
        height: 400,
        backgroundColor: "transparent",
        chartArea: {
          fill: "transparent",
          backgroundColor: "transparent",
        },
        // Podešavanje linija na vertikalnoj osi
        vAxis: {
          gridlines: {
            color: "oklch(0.67 0 0 / 0.41)", // Boja horizontalnih linija
            count: 4, // Opciono: broj linija
          },
          baselineColor: "#888", // Boja nulte (donje) linije
        },

        // Uklanjanje vertikalnih linija (obično lepše izgleda)
        hAxis: {
          gridlines: { color: "transparent" },
        },

        legend: { position: "none" },
      };

      var chart = new google.charts.Line(document.getElementById("chart_div2"));
      chart.draw(data, google.charts.Line.convertOptions(options));
    }
  }

  function processPieChart(){
      function drawPieChart() {
      // 1. Podaci (možeš ih puniti iz fetch-a kao i prošli put)
        var data = google.visualization.arrayToDataTable([
          ['Kategorija', 'Narudjbe'],
          ['Restoran',     ordersRestoran],
          ['Dostava',      ordersDostava],
          ['Takeaway',  ordersTakeAway]
        ]);

        // 2. Opcije za dizajn
        var options = {
          //title: 'Tip narudžbina',
          backgroundColor: 'transparent',
          colors: ['#1b9e77', '#d95f02', '#7570b3'], // Tvoje boje
          chartArea: { width: '70%', height: '70%' },
          width: '100%',
          height: 500, // Povećaj sa 400 na 500 ili više
          
          // 3. Podešavanje boja teksta
          titleTextStyle: {
              color: '#FFFFFF', // Boja naslova
              fontSize: 20,
              bold: true
          },
          legend: {
              position: 'bottom',
              textStyle: {
                  color: '#CCCCCC', // Boja teksta legende
                  fontSize: 14
              }
          },
          pieSliceTextStyle: {
              color: '#000000', // Boja brojeva unutar isečaka
          },
        };

        // 3. Crtanje
        var chart = new google.visualization.PieChart(document.getElementById('piechart_div'));
        chart.draw(data, options);
    }

    // Učitavanje biblioteke
    google.charts.load('current', {'packages':['corechart']});
    google.charts.setOnLoadCallback(drawPieChart);
    }

  async function loadOrders() 
  {
    const token = $auth.token;

    try 
    {
      const res = await fetch(API_BASE_URL + `/orders`, 
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      // Check response status and handle specific cases
      if (!res.ok) 
      {        
        throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
      }

      // Parse JSON directly - no need for JSON.parse since res.json() already does this
      const data = await res.json();

      // Update orders with the received data
      orders = data;

      orders = data.reverse();


      ordersRestoran = orders.filter((o) => o.code.endsWith('R')).length;
      ordersDostava = orders.filter((o) => o.code.endsWith('D')).length;
      ordersTakeAway = orders.filter((o) => o.code.endsWith('Z')).length;
      processPieChart();
    } 
    catch (error: any) 
    {
      console.error("Error loading orders:", error);

      /**
       * TODO: see if this works. should display error message.
       * ako je failed to fetch, server je nedostupan.
       */

      // Handle 401 Unauthorized specifically
      if (error.message.includes("401")) 
      {
        console.log("Authentication failed - token may be expired");
        // Clear invalid token
        $auth.token = null;
        // Redirect to login or show login modal
        // window.location.href = '/login';
        // OR: showLoginModal = true;
        // OR: goto('/login');
      }

      // Handle other errors appropriately (show user message, etc.)
    } 
    
  }

  async function loadTimesChart() {
    // 1. Učitaj Google Charts biblioteku
    google.charts.load('current', { 'packages': ['corechart'] });
    google.charts.setOnLoadCallback(fetchAndDraw);

    async function fetchAndDraw() {
        try {
            // 2. Tvoj fetch poziv
            const res = await fetch(API_BASE_URL + `/statistics/total-times`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!res.ok) throw new Error("Neuspešan fetch podataka");

            const jsonData = await res.json();

            // 3. Mapiranje podataka za Stacked Bar Chart
            // Format: [Oznaka, Čekanje, Priprema]
            const chartData = [['Order Code', 'Čekanje (min)', 'Priprema (min)']];

            jsonData.forEach(item => {
                // Uzimamo samo narudžbine koje imaju izračunata vremena
                if (item.cekanje_minuti != null && item.priprema_minuti != null 
                    && item.cekanje_minuti < 60 
                    && item.priprema_minuti < 60 
                ) {
                    chartData.push([
                        item.code_datum,
                        // Zaokružujemo na 2 decimale jer su ti podaci u JSON-u sirovi
                        parseFloat(item.cekanje_minuti.toFixed(2)),
                        parseFloat(item.priprema_minuti.toFixed(2))
                    ]);
                }
            });

            console.log(chartData)

            // 4. Crtanje grafikona
            drawChart(chartData);

        } catch (error) {
            console.error("Greška pri učitavanju grafikona efikasnosti:", error);
        }
    }

   function drawChart(formattedData: string[][]) {
    var data = google.visualization.arrayToDataTable(formattedData);

    var options = {
        title: 'Analiza vremena pripreme po narudžbini',
        titleTextStyle: {
            color: '#FFFFFF',
            fontSize: 20,
            bold: true
        },
        height: 500,
        isStacked: true, 
        backgroundColor: 'transparent',
        colors: ['#E67E22', 'rgb(62, 99, 221)'], 
        chartArea: { 
            width: '80%', // Povećano malo radi bolje preglednosti u vertikalnom položaju
            height: '70%', 
            fill: "transparent",
            backgroundColor: "transparent",
        },
        hAxis: {
            title: 'Kod narudžbine', // Sada je kod narudžbine dole
            textStyle: { color: "oklch(0.67 0 0 / 0.41)", fontSize: 12 },
            gridlines: { color: 'transparent' },
        },
        vAxis: {
            title: 'Minuti', // Minuti su sada sa strane (vertikalno)
            minValue: 0,
            textStyle: { color: "oklch(0.67 0 0 / 0.41)" },
            gridlines: { color: 'transparent' },                
        },
        legend: { 
            position: 'top',
            textStyle: { color: '#FFFFFF' } // Dodato da se legenda vidi na tamnoj pozadini
        },
        animation: {
            duration: 1000,
            easing: 'out',
            startup: true
        },
    };

    // KLJUČNA IZMJENA: ColumnChart umjesto BarChart
    var chart = new google.visualization.ColumnChart(document.getElementById('chart_div3'));
    chart.draw(data, options);
}
}

</script>

<div class="stats-wrapper">

  <div class="stats-container">

    <!-- Google Chart -->
    <div class="chart-card">
      <div id="chart_div1"></div>
    </div>

    <div class="chart-card">
      <div id="chart_div2"></div>
    </div>

  </div>
   <div class="stats-container grid grid-cols-1 lg:grid-cols-3 gap-4">

    <div class="chart-card lg:col-span-2">
      <div id="chart_div3"></div>
    </div>

    <div class="chart-card">
      <h2 class="text-primary/80">Tip narudžbi</h2>
      <div class="chart-container">
        <div id="piechart_div"></div>
      </div>
    </div>

</div>
</div>

<style>
  .chart-container {
    display: flex;
    align-items: center;
    font-family: sans-serif;
    gap: 20px;
  }


  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    padding: 2rem 1rem;
    margin: 0 auto;
    margin-top: -3rem;
  }

  @keyframes slideIn {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  @media (max-width: 640px) {
    .stats-container {
      grid-template-columns: 1fr;
      padding: 1rem;
    }
  }

  .stats-wrapper {
    width: 100%;
    padding: 20px;
  }

  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 20px;
    margin: 0 auto;
  }

  .chart-card {
    background: var(--color-base-200);
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .chart-card h2 {
    margin-top: 0;
    margin-bottom: 20px;
    font-size: 1.5rem;
  }

  .chart-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }

  /* Mobile optimization */
  @media (max-width: 768px) {
    .stats-wrapper {
      padding: 10px;
    }

    .stats-container {
      grid-template-columns: 1fr;
      gap: 15px;
    }

    .chart-card {
      padding: 15px;
    }

    .chart-card h2 {
      font-size: 1.25rem;
    }

  }

</style>