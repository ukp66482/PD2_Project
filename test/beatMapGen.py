import os
import librosa
import numpy as np

notes = [
    ('C1', 33), ('D1', 37), ('E1', 41), ('F1', 44), ('G1', 49), ('A1', 55), ('B1', 62),
    ('C2', 65), ('D2', 73), ('E2', 82), ('F2', 87), ('G2', 98), ('A2', 110), ('B2', 123),
    ('C3', 131), ('D3', 147), ('E3', 165), ('F3', 175), ('G3', 196), ('A3', 220), ('B3', 247),
    ('C4', 262), ('D4', 294), ('E4', 330), ('F4', 349), ('G4', 392), ('A4', 440), ('B4', 494),
    ('C5', 523), ('D5', 587), ('E5', 659), ('F5', 698), ('G5', 784), ('A5', 880), ('B5', 988),
    ('C6', 1047), ('D6', 1175), ('E6', 1319), ('F6', 1397), ('G6', 1568), ('A6', 1760), ('B6', 1976),
    ('C7', 2093), ('D7', 2349), ('E7', 2637), ('F7', 2794), ('G7', 3136), ('A7', 3520), ('B7', 3951),
    ('C8', 4186), ('D8', 4699), ('E8', 5274), ('F8', 5588), ('G8', 6272), ('A8', 7040), ('B8', 7902)
]

directory = './'
audio_files = [f for f in os.listdir(directory) if f.endswith('.mp3')]

def process_audio_file(audio_path):
    y, sr = librosa.load(audio_path)

    S = np.abs(librosa.stft(y))
    frequencies = librosa.fft_frequencies(sr=sr)

    onset_env = librosa.onset.onset_strength(y=y, sr=sr, aggregate=np.median)
    onsets = librosa.onset.onset_detect(onset_envelope=onset_env, sr=sr, backtrack=True, units='time', hop_length=512)

    min_interval = 0.1  # in seconds
    filtered_onsets = [onsets[0]]
    for onset in onsets[1:]:
        if onset - filtered_onsets[-1] >= min_interval:
            filtered_onsets.append(onset)

    onset_spectra = []
    dominant_frequencies = []

    for onset in filtered_onsets:
        onset_frame = librosa.time_to_frames(onset, sr=sr)
        spectrum = np.abs(S[:, onset_frame])
        onset_spectra.append(spectrum)

        dominant_freq_index = np.argmax(spectrum)
        dominant_frequency = frequencies[dominant_freq_index]
        dominant_frequencies.append(dominant_frequency)

    def find_closest_note(freq, notes_freq):
        return min(notes_freq, key=lambda x: abs(x[1] - freq))

    keys = []
    for onset, dom_freq in zip(filtered_onsets, dominant_frequencies):
        closest_note, closest_freq = find_closest_note(dom_freq, notes)
        key = {'time': onset, 'button': ord(closest_note[0]) - ord('A') + 1}
        keys.append(key)

    return keys

output_file = 'beatmap.txt'
with open(output_file, 'w') as file:
    for audio_file in audio_files:
        audio_path = os.path.join(directory, audio_file)
        keys = process_audio_file(audio_path)
        for key in keys:
            file.write(f"{key['time']:.3f} {key['button']}\n")
