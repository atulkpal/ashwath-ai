# Ashwath AI Worktree Setup
# Run from repo root to create isolated checkouts for each workspace.
# Each worktree has its own feature branch — commit independently, merge to main.
# See AGENT.md for branch and merge policy.

$ROOT = Resolve-Path (Split-Path $MyInvocation.MyCommand.Path -Parent)/..
$WORKTREES = "$ROOT/worktrees"

$workspaces = @(
    @{ Name = "platform";   Branch = "feature/platform" }
    @{ Name = "android";    Branch = "feature/android-client" }
    @{ Name = "web";        Branch = "feature/web-client" }
    @{ Name = "research";   Branch = "feature/research" }
    @{ Name = "release";    Branch = "feature/release" }
    # Uncomment when work begins:
    # @{ Name = "ios";      Branch = "feature/ios" }
    # @{ Name = "desktop";  Branch = "feature/desktop" }
)

foreach ($w in $workspaces) {
    $path = "$WORKTREES/$($w.Name)"
    if (Test-Path $path) {
        Write-Host "Worktree $($w.Name) already exists at $path" -ForegroundColor Yellow
    } else {
        git worktree add $path $w.Branch
        Write-Host "Created worktree $($w.Name) at $path" -ForegroundColor Green
    }
}

Write-Host "`nAll worktrees created under $WORKTREES"
Write-Host "Each directory is a full git checkout on its feature branch."
Write-Host "Commit in any worktree, push from any worktree — they share the same repository."
