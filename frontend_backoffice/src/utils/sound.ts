// utils/sound.ts
// zvuk koji koristimo za notifikaciju

export const playNotificationSound = (type: 'info' | 'success' | 'error' | 'warning'): void => {
  // Možeš koristiti istu datoteku za sve, ili različite ako ih dodaš kasnije
  const soundFile = type === 'error' ? 'error.mp3' : 'notification.mp3';
  
  const audio = new Audio(`/sound/${soundFile}`);
  
  // Pojačavamo na max ako je sistemski tiho
  audio.volume = 0.8; 

  audio.play().catch(err => {
    // Ovo se obično događa ako korisnik još nije kliknuo nigdje na stranici
    console.warn("Audio playback blocked by browser:", err);
  });
};