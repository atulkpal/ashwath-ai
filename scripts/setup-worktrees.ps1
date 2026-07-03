# AshwathAI Worktree Setup
# Run from repo root to create isolated checkouts for each frontend.
# Each worktree is a separate directory with its own branch,
# so teams can work independently without touching engine code.

$ROOT = Resolve-Path (Split-Path $MyInvocation.MyCommand.Path -Parent)/..
$WORKTREES = "$ROOT/worktrees"

$frontends = @(
    @{ Name = "android-installer"; Branch = "feature/android-installer" }
    @{ Name = "web-frontend";      Branch = "feature/web-frontend" }
    @{ Name = "ios-frontend";      Branch = "feature/ios-frontend" }
    @{ Name = "desktop-frontend";  Branch = "feature/desktop-frontend" }
)

foreach ($f in $frontends) {
    $path = "$WORKTREES/$($f.Name)"
    if (Test-Path $path) {
        Write-Host "Worktree $($f.Name) already exists at $path" -ForegroundColor Yellow
    } else {
        git worktree add $path $f.Branch
        Write-Host "Created worktree $($f.Name) at $path" -ForegroundColor Green
    }
}

Write-Host "`nAll worktrees created under $WORKTREES"
Write-Host "Each directory is a full git checkout on its feature branch."
Write-Host "Commit in any worktree, push from any worktree — they share the same repository."
