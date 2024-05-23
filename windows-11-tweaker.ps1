$codes = "Unchanged", "Add", "Remove"
$commands = ("Disable new context menu", 0, "reg add `"HKCU\Software\Classes\CLSID\{86ca1aa0-34aa-4e8b-a509-50c905bae2a2}\InprocServer32`" /ve /f", "reg delete `"HKCU\Software\Classes\CLSID\{86ca1aa0-34aa-4e8b-a509-50c905bae2a2}\InprocServer32`""),
("Disable search box suggestions", 0, "reg add `"HKCU\Software\Policies\Microsoft\Windows\Explorer`" /v DisableSearchBoxSuggestions /t REG_DWORD /d 1 /f", "reg add `"HKCU\Software\Policies\Microsoft\Windows\Explorer`" /v DisableSearchBoxSuggestions /t REG_DWORD /d 0 /f")
$selected = 0
$key = 0

function quit() {
	pause
	exit
}

function apply() {
	[Console]::Clear()
	Write-Host "Applying..."
	Write-Host
	$n = 0
	foreach ($i in $commands) {
		if ($i[1] -ne 0) {
			Write-Host ($i[0] + ": " + $codes[$i[1]])
			$reg = $i[1 + $i[1]]
			Write-Host $reg
			Write-Host
			try {
				Invoke-Expression $reg
			} catch {
				Write-Host $_
				quit
			}
			$n++
		}
	}
	if ($n -ne 0) {
		taskkill /f /im explorer.exe
		Start-Process explorer.exe
	} else {
		Write-Host "Nothing to apply!"
	}
	quit
}

function update() {
	[Console]::Clear()
	Write-Host "Mocha's Windows 11 tweaker!"
	Write-Host "https://github.com/mochawoof/mochatoys"
	Write-Host "~~~~~~~~~~~~~~~~~~~~~~~~~~"
	Write-Host
	Write-Host "Use the arrow keys to select and change, enter to apply them or Q to quit."

	for ($i=0; $i -lt $commands.Count; $i++) {
		$cmd = $commands[$i]
		$write = $cmd[0] + ": " + $codes[$cmd[1]]
		if ($selected -eq $i) {
			$write = "> " + $write
		}
		Write-Host $write
	}
	Write-Host
	$key = [Console]::ReadKey().Key
	switch ($key) {
		([ConsoleKey]::Q) {
			quit
		}
		([ConsoleKey]::UpArrow) {
			if ($selected -eq 0) {
				$selected = $commands.Count - 1
			} else {
				$selected--
			}
		}
		([ConsoleKey]::DownArrow) {
			if ($selected -eq $commands.Count - 1) {
				$selected = 0
			} else {
				$selected++
			}
		}
		([ConsoleKey]::LeftArrow) {
			$cmd = $commands[$selected]
			if ($cmd[1] -eq 0) {
				$commands[$selected][1] = $codes.Count - 1
			} else {
				$commands[$selected][1]--
			}
		}
		([ConsoleKey]::RightArrow) {
			$cmd = $commands[$selected]
			if ($cmd[1] -eq $codes.Count - 1) {
				$commands[$selected][1] = 0
			} else {
				$commands[$selected][1]++
			}
		}
		([ConsoleKey]::Enter) {
			apply
		}
	}
	update
}

#Check for admin
$currentPrincipal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
if ($currentPrincipal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
	update
} else {
	try {
		Start-Process powershell $PSCommandPath -Verb runAs
	} catch {
		Write-Host "This script needs admin rights to work correctly!"
		quit
	}
}