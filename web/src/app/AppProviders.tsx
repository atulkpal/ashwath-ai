import type { ReactNode } from "react";
import { EngineProvider } from "@/engine";

type Props = {
  children: ReactNode;
};

export function AppProviders({ children }: Props) {
  return <EngineProvider>{children}</EngineProvider>;
}