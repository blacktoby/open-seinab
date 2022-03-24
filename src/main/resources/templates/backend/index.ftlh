<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html lang="de">
<head>
    <#include 'includes/meta.ftlh'>

    <title>Seinab Backend</title>

    <#include 'includes/style.ftlh'>
</head>
<body id="page-top">
<!-- Page Wrapper -->
<div id="wrapper">

    <#include 'includes/sidebar.ftlh'>

    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

        <!-- Main Content -->
        <div id="content">

            <#include 'includes/topbar.ftlh'>

            <!-- Begin Page Content -->
            <div class="container-fluid">

                <!-- Page Heading -->
                <h1 class="h3 mb-4 text-gray-800">Willkommen in der Verwaltung von Seinab!</h1>
                <p>Werfe einen Blick auf deine Seitennavigation um deine Veranstaltungen zu verwalten.</p>

            </div>
            <!-- /.container-fluid -->

        </div>
        <!-- End of Main Content -->

        <#include 'includes/footer.ftlh'>

    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<!-- Scroll to Top Button-->
<a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
</a>

<#include 'includes/logout.ftlh'>

<#include 'includes/scripts.ftlh'>
</body>
</html>