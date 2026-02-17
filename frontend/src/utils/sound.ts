// utils/sound.ts
// zvuk koji koristimo za notifikaciju

export function playNotificationSound(type: 'success' | 'error' | 'info' = 'info') 
{
  const ctx = new AudioContext();
  const gainNode = ctx.createGain();
  gainNode.connect(ctx.destination);

  function playTone(frequency: number, startTime: number, duration: number, volume: number = 0.4) {
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.connect(gain);
    gain.connect(ctx.destination);

    osc.type = 'sine';
    osc.frequency.setValueAtTime(frequency, startTime);

    gain.gain.setValueAtTime(0, startTime);
    gain.gain.linearRampToValueAtTime(volume, startTime + 0.02); // attack
    gain.gain.setValueAtTime(volume, startTime + duration - 0.1);
    gain.gain.exponentialRampToValueAtTime(0.001, startTime + duration); // release

    osc.start(startTime);
    osc.stop(startTime + duration);
  }

  switch (type) {
    case 'success':
      // Tri tona gore - veselo zvono
      playTone(523, ctx.currentTime,        0.25, 0.35); // C5
      playTone(659, ctx.currentTime + 0.18, 0.25, 0.35); // E5
      playTone(784, ctx.currentTime + 0.36, 0.40, 0.40); // G5
      playTone(1046,ctx.currentTime + 0.60, 0.50, 0.30); // C6 - visoki završni ton
      break;

    case 'error':
      // Duboki silazni tonovi - ozbiljno upozorenje
      playTone(440, ctx.currentTime,        0.30, 0.45); // A4
      playTone(370, ctx.currentTime + 0.25, 0.30, 0.45); // F#4
      playTone(294, ctx.currentTime + 0.50, 0.50, 0.50); // D4
      playTone(220, ctx.currentTime + 0.85, 0.60, 0.40); // A3 - završni duboki ton
      break;

    case 'info':
    default:
      // Dva blaga tona - neutralna notifikacija
      playTone(660, ctx.currentTime,        0.30, 0.35); // E5
      playTone(880, ctx.currentTime + 0.25, 0.45, 0.30); // A5
      break;
  }
}