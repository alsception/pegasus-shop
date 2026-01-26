<script>
// @ts-nocheck

  /**
   * @type {any[] | null | undefined}
   */
   export let dailyReservations = [];

  // @ts-ignore
  const maxGuests = Math.max(...dailyReservations.map(d => d.totalGuests));
  // @ts-ignore
  const maxReservations = Math.max(...dailyReservations.map(d => d.totalReservations));
  const width = 800;
  const height = 300;
  const padding = 60;
  const chartWidth = width - padding * 2;
  const chartHeight = height - padding * 2;

  const formatDate = (/** @type {string | number | Date} */ dateStr) => {
    const date = new Date(dateStr);
    return `${date.getDate()}/${date.getMonth() + 1}`;
  };

 // @ts-ignore
   $: guestPoints = dailyReservations.map((day, i) => {
    // @ts-ignore
    const x = padding + (i / (dailyReservations.length - 1)) * chartWidth;
    const y = padding + chartHeight - (day.totalGuests / maxGuests) * chartHeight;
    return `${x},${y}`;
  }).join(' ');

 // @ts-ignore
   $: reservationPoints = dailyReservations.map((day, i) => {
    // @ts-ignore
    const x = padding + (i / (dailyReservations.length - 1)) * chartWidth;
    const y = padding + chartHeight - (day.totalReservations / maxReservations) * chartHeight;
    return `${x},${y}`;
  }).join(' ');

 // @ts-ignore
   $: avgGuests = Math.round(dailyReservations.reduce((sum, d) => sum + d.totalGuests, 0) / dailyReservations.length);
 // @ts-ignore
   $: totalStandard = dailyReservations.reduce((sum, d) => sum + d.totalMenuStandard, 0);
 // @ts-ignore
   $: totalGold = dailyReservations.reduce((sum, d) => sum + d.totalMenuGold, 0);
 // @ts-ignore
   $: totalPremium = dailyReservations.reduce((sum, d) => sum + d.totalMenuPremium, 0);
</script>

<div class="w-full max-w-4xl mx-auto p-6 bg-slate-900 rounded-lg">
  <h2 class="text-2xl font-bold text-white mb-6">Dnevni pregled rezervacija</h2>
  
  <svg viewBox="0 0 {width} {height}" class="w-full">
    <!-- Grid lines -->
    {#each [0, 0.25, 0.5, 0.75, 1] as ratio, i}
      <line
        x1={padding}
        y1={padding + chartHeight * ratio}
        x2={width - padding}
        y2={padding + chartHeight * ratio}
        stroke="#334155"
        stroke-width="1"
        stroke-dasharray="4"
      />
      <text
        x={padding - 10}
        y={padding + chartHeight * ratio + 5}
        text-anchor="end"
        fill="#94a3b8"
        font-size="12"
      >
        {Math.round(maxGuests * (1 - ratio))}
      </text>
    {/each}

    <!-- X axis labels -->
    {#each dailyReservations as day, i}
      <text
        x={padding + (i / (dailyReservations.length - 1)) * chartWidth}
        y={height - padding + 20}
        text-anchor="middle"
        fill="#94a3b8"
        font-size="12"
      >
        {formatDate(day.date)}
      </text>
    {/each}

    <!-- Total Guests line -->
    <polyline
      points={guestPoints}
      fill="none"
      stroke="#00ff9f"
      stroke-width="3"
      stroke-linecap="round"
      stroke-linejoin="round"
    />

    <!-- Total Reservations line -->
    <polyline
      points={reservationPoints}
      fill="none"
      stroke="#0ff"
      stroke-width="3"
      stroke-linecap="round"
      stroke-linejoin="round"
    />

    <!-- Data points -->
    {#each dailyReservations as day, i}
      {@const x = padding + (i / (dailyReservations.length - 1)) * chartWidth}
      {@const yGuests = padding + chartHeight - (day.totalGuests / maxGuests) * chartHeight}
      <circle cx={x} cy={yGuests} r="5" fill="#00ff9f">
        <title>{formatDate(day.date)}: {day.totalGuests} gostiju</title>
      </circle>
    {/each}
  </svg>

  <!-- Legend -->
  <div class="flex gap-6 mt-4 justify-center">
    <div class="flex items-center gap-2">
      <div class="w-4 h-4 bg-[#00ff9f] rounded-full"></div>
      <span class="text-sm text-gray-300">Ukupno gostiju</span>
    </div>
    <div class="flex items-center gap-2">
      <div class="w-4 h-4 bg-[#0ff] rounded-full"></div>
      <span class="text-sm text-gray-300">Ukupno rezervacija</span>
    </div>
  </div>

  <!-- Stats -->
  <div class="grid grid-cols-4 gap-4 mt-6">
    <div class="bg-slate-800 p-4 rounded">
      <div class="text-gray-400 text-sm">Prosječno gostiju</div>
      <div class="text-2xl font-bold text-[#00ff9f]">{avgGuests}</div>
    </div>
    <div class="bg-slate-800 p-4 rounded">
      <div class="text-gray-400 text-sm">Standard</div>
      <div class="text-2xl font-bold text-blue-400">{totalStandard}</div>
    </div>
    <div class="bg-slate-800 p-4 rounded">
      <div class="text-gray-400 text-sm">Gold</div>
      <div class="text-2xl font-bold text-yellow-400">{totalGold}</div>
    </div>
    <div class="bg-slate-800 p-4 rounded">
      <div class="text-gray-400 text-sm">Premium</div>
      <div class="text-2xl font-bold text-purple-400">{totalPremium}</div>
    </div>
  </div>
</div>