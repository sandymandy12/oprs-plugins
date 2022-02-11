param ($name)

$externaldir="$($HOME)/.openosrs/externalmanager"
$pluginName = $name.replace(" ", "")
$plugindir = $pluginName.ToLower()
$srcPath = $plugindir + "/src/main/java/net/runelite/client/plugins/" + $plugindir

mkdir $srcPath
cd $srcPath

$files = @(
    "$($pluginName)Plugin.java",
    "$($pluginName)Config.java",
    "$($pluginName)Overlay.java"
)

for($i = 0; $i -lt $files.length; $i++){
    if(-not(Test-Path $files[$i])){
        # It does not exist. Create it
        New-Item -ItemType File -Name $files[$i]}

    else {
        #It exists. Update the timestamp
        (Get-ChildItem $files[$i]).LastWriteTime=Get-Date
    }
}

ii .

# write-host $srcPath

# if (-not(Test-Path -Path Alias:Touch)) {
#     New-Alias -Name Touch Touch-File -Force
# }