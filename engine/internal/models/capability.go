package models

import "math"

func ScoreModels(device DeviceSpec, models []Model) []ScoredModel {
	if len(models) == 0 {
		return nil
	}

	scored := make([]ScoredModel, 0, len(models))

	for _, m := range models {
		score, reason := scoreModel(device, m)
		scored = append(scored, ScoredModel{
			Model:       m,
			Score:       score,
			Recommended: score >= 80,
			Reason:      reason,
		})
	}

	return scored
}

func scoreModel(device DeviceSpec, m Model) (int, string) {
	if device.RamGB < m.MinRamGB {
		shortfall := m.MinRamGB - device.RamGB
		if shortfall > 2 {
			return 0, "needs " + formatGB(m.MinRamGB) + " RAM (device has " + formatGB(device.RamGB) + ")"
		}
		return 30, "limited — needs " + formatGB(m.MinRamGB) + " RAM"
	}

	var score float64

	if device.RamGB >= m.RecommendedRamGB {
		score = 90
	} else {
		ratio := device.RamGB / m.RecommendedRamGB
		score = 50 + math.Min(ratio, 1.0)*40
	}

	if device.HasNPU {
		score += 10
	} else if device.HasGPU {
		score += 5
	}

	if device.RamGB >= m.RecommendedRamGB+4 {
		score -= 5
	}

	if device.CPUCores >= 8 {
		score += 3
	}

	score = math.Max(0, math.Min(100, score))

	var reason string
	switch {
	case score >= 90:
		reason = "excellent match for your device"
	case score >= 80:
		reason = "great match for your device"
	case score >= 60:
		reason = "compatible — may have reduced performance"
	case score >= 30:
		reason = "limited by device RAM"
	default:
		reason = "not recommended for this device"
	}

	return int(math.Round(score)), reason
}

func formatGB(gb float64) string {
	return formatFloat(gb) + " GB"
}

func formatFloat(f float64) string {
	if f == float64(int(f)) {
		return intToStr(int(f))
	}
	return floatToStr(f)
}

func intToStr(i int) string {
	if i == 0 {
		return "0"
	}
	d := ""
	for i > 0 {
		d = string(rune('0'+i%10)) + d
		i /= 10
	}
	return d
}

func floatToStr(f float64) string {
	i := int(f)
	r := int((f - float64(i)) * 10)
	if r < 0 {
		r = -r
	}
	return intToStr(i) + "." + intToStr(r)
}
