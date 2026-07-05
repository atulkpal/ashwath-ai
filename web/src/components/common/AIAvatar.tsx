type AIAvatarProps = {
  size?: "sm" | "md"
}

export function AIAvatar({ size = "md" }: AIAvatarProps) {
  const dim = size === "sm" ? "size-6" : "size-8"
  const fontSize = size === "sm" ? "text-[9px]" : "text-[10px]"
  const radius = size === "sm" ? "rounded-md" : "rounded-lg"

  return (
    <div className={`${dim} ${radius} bg-[#00f0ff]/10 border border-[#00f0ff]/20 flex items-center justify-center shrink-0`}>
      <span className={`${fontSize} font-bold text-[#00f0ff]`}>AI</span>
    </div>
  )
}
